package com.iotiq.commons.config;

import com.iotiq.commons.exceptions.ApplicationException;
import com.iotiq.commons.message.response.ValidationError;
import com.iotiq.commons.util.LoggingUtils;
import com.iotiq.commons.util.MessageUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static com.iotiq.commons.util.MessageUtil.getSeries;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private final ResourceBundleMessageSource messageSource;
    private final MessageUtil messageUtil;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException exception, @NonNull WebRequest request) {
        HttpStatusCode status = exception.getStatus();

        if (!HttpStatus.BAD_GATEWAY.equals(status)) {
            logException(request, exception);
        }

        String defaultDetail = messageSource.getMessage(exception, getLocale());
        String messageCode = ErrorResponse.getDefaultDetailMessageCode(ApplicationException.class, null);
        Object[] arguments = exception.getArguments();

        ProblemDetail problemDetail = this.createProblemDetail(exception, status, defaultDetail, messageCode, arguments, request);
        return this.handleExceptionInternal(exception, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException exception, @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status, @NotNull WebRequest request
    ) {
        List<ValidationError> validationErrors = getValidationErrors(exception);
        ProblemDetail problemDetail = createProblemDetail(exception, status, "validation failed",
                "validation", exception.getDetailMessageArguments(), request);
        problemDetail.setProperty("validation", validationErrors);
        return handleExceptionInternal(exception, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            @NotNull HandlerMethodValidationException exception, @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status, @NotNull WebRequest request
    ) {
        List<ValidationError> validationErrors = getValidationErrors(exception.getAllErrors());
        ProblemDetail problemDetail = this.createProblemDetail(exception, status, "validation failed",
                ErrorResponse.getDefaultDetailMessageCode(ApplicationException.class, null),
                exception.getDetailMessageArguments(), request);
        problemDetail.setProperty("validation", validationErrors);
        return handleExceptionInternal(exception, problemDetail, headers, status, request);
    }

    @Override
    protected @NotNull ProblemDetail createProblemDetail(
            @NotNull Exception exception, @NotNull HttpStatusCode status, @NotNull String defaultDetail,
            String detailMessageCode, Object[] detailMessageArguments, @NotNull WebRequest request
    ) {
        HttpStatus.Series series = getSeries(status);
        ProblemDetail problemDetail = super.createProblemDetail(exception, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
        problemDetail.setProperty("series", series);
        problemDetail.setProperty("rootCause", ExceptionUtils.getRootCause(exception).toString());
        return problemDetail;
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


    private List<ValidationError> getValidationErrors(Errors exception) {
        final List<FieldError> fieldErrors = exception.getFieldErrors();

        return fieldErrors.stream()
                .map(fieldError -> ValidationError.builder()
                        .field(fieldError.getField())
                        .rejectedValue(fieldError.getRejectedValue())
                        .message(messageUtil.getErrorMessage(fieldError))
                        .build())
                .toList();
    }

    @NotNull
    private List<ValidationError> getValidationErrors(List<? extends MessageSourceResolvable> allErrors) {
        return allErrors.stream()
                .map(err -> ValidationError.builder()
                        .message(messageUtil.getErrorMessage(err))
                        .build())
                .toList();
    }
}
