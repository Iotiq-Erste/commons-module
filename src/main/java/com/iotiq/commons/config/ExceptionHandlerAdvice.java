package com.iotiq.commons.config;

import com.iotiq.commons.exceptions.ApplicationException;
import com.iotiq.commons.message.response.ValidationError;
import com.iotiq.commons.util.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.iotiq.commons.util.HttpUtil.getSeries;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private final ResourceBundleMessageSource messageSource;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException exception, @NonNull WebRequest request) {
        logException(request, exception);

        String defaultDetail = messageSource.getMessage(exception, getLocale());
        String messageCode = ErrorResponse.getDefaultDetailMessageCode(ApplicationException.class, (String) null);
        HttpStatusCode status = exception.getStatus();
        Object[] arguments = exception.getArguments();

        ProblemDetail problemDetail = this.createProblemDetail(exception, status, defaultDetail, messageCode, arguments, request);
        return this.handleExceptionInternal(exception, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {

        List<ValidationError> validationErrors = getValidationErrors(exception);
        ProblemDetail problemDetail = createProblemDetail(exception, status, "validation failed",
                "validation", exception.getDetailMessageArguments(), request);
        problemDetail.setProperty("validation", validationErrors);
        return handleExceptionInternal(exception, problemDetail, headers, status, request);
    }

    @ExceptionHandler(Throwable.class)
    ProblemDetail onThrowable(Throwable error, HttpServletRequest request) {
        logger.error(ExceptionUtils.getStackTrace(error));
        ServerHttpObservationFilter.findObservationContext(request)
                .ifPresent(context -> context.setError(error));

        return createProblemDetail(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @NotNull ProblemDetail createProblemDetail(
            @NotNull Exception exception, @NotNull HttpStatusCode status, @NotNull String defaultDetail,
            String detailMessageCode, Object[] detailMessageArguments, @NotNull WebRequest request
    ) {
        HttpStatus.Series series = getSeries(status);
        ProblemDetail problemDetail = super.createProblemDetail(exception, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
        addProblemDetailProperties(problemDetail, series, exception);
        return problemDetail;
    }

    private ProblemDetail createProblemDetail(Throwable error, HttpStatus status) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(error.toString());
        addProblemDetailProperties(problemDetail, status.series(), error);
//        problemDetail.setProperty("traceparent", getTraceParent());

        return problemDetail;
    }

    private static void addProblemDetailProperties(ProblemDetail problemDetail, HttpStatus.Series status, Throwable error) {
        problemDetail.setProperty("series", status);
        problemDetail.setProperty("rootCause", ExceptionUtils.getRootCause(error).toString());
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
                        .message(getMessage(fieldError))
                        .build())
                .collect(Collectors.toList());
    }

    private String getMessage(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage();

        if (defaultMessage != null) {
            try {
                String defaultMessageResolved = messageSource.getMessage(defaultMessage, fieldError.getArguments(), getLocale());
                if (!defaultMessageResolved.equals(defaultMessage)) {
                    return defaultMessageResolved;
                }
            } catch (NoSuchMessageException e) {
                return defaultMessage;
            }
        }
        return messageSource.getMessage(fieldError, getLocale());
    }

}
