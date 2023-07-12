package com.iotiq.commons.config;

import com.iotiq.commons.exceptions.ApplicationException;
import com.iotiq.commons.message.response.ErrorMessageDto;
import com.iotiq.commons.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    private static final String EXCEPTION_GENERIC = "exception.generic";
    private final ResourceBundleMessageSource messageSource;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handleValidationException(MethodArgumentNotValidException exception, @NonNull WebRequest request) {
        logException(request, exception);
        Set<String> messages = exception.getBindingResult().getFieldErrors()//Validations are duplicated somehow.
                .stream()
                .filter(it -> org.springframework.util.StringUtils.hasText(it.getDefaultMessage()))
                .map(fieldError -> getDescription(fieldError.getDefaultMessage())).collect(Collectors.toSet());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorMessageDto.builder()
                        .requestId(LoggingUtils.getExistingRequestId())
                        .messages(new ArrayList<>(messages)).build());
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception, @NonNull WebRequest request) {
        Throwable cause = exception.getCause();

        if (cause instanceof ConstraintViolationException constraintViolationException) {
            SQLException sqlException = constraintViolationException.getSQLException();
            String sqlMessage = sqlException.getMessage();
            Pattern pattern = Pattern.compile("^(?:.+|\n+)(Detail:)(?<messageDetail>.+)$");
            Matcher matcher = pattern.matcher(sqlMessage);

            if (matcher.find()) {
                String messageDetail = matcher.group("messageDetail");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ErrorMessageDto.builder()
                                .messages(Collections.singletonList(getDescription("exception.hibernateConstraintViolation", messageDetail)))
                                .requestId(LoggingUtils.getExistingRequestId())
                                .build());
            }
        }

        return handle(exception, request);
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ErrorMessageDto> handleApplicationException(ApplicationException exception, @NonNull WebRequest request) {
        return logAndPrepareResponse(exception, request, exception.getStatus(), messageSource.getMessage(exception, getLocale()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessageDto> handle(Exception exception, @NonNull WebRequest request) {
        return logAndPrepareResponse(exception, request, HttpStatus.INTERNAL_SERVER_ERROR, (EXCEPTION_GENERIC));
    }

    private ResponseEntity<ErrorMessageDto> logAndPrepareResponse(Exception exception, WebRequest request, HttpStatus status, String description) {
        logException(request, exception);
        return ResponseEntity.status(status)
                .body(ErrorMessageDto.builder()
                        .messages(Collections.singletonList(getDescription(description)))
                        .requestId(LoggingUtils.getExistingRequestId())
                        .build());
    }

    void logException(WebRequest request, Exception ex) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        final String message = String.format(
                "caught %s during %s to %s",
                ex.getClass().getName(),
                servletWebRequest.getHttpMethod(),
                servletWebRequest.getRequest().getServletPath()
        );
        LoggingUtils.error(message, ex);
    }

    String getDescription(String key, String... args) {
        try {
            return messageSource.getMessage(key, args, getLocale());
        } catch (NoSuchMessageException ex) {
            return key;//If it is not defined
        }
    }
}
