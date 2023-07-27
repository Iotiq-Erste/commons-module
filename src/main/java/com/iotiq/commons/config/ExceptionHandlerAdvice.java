package com.iotiq.commons.config;

import com.iotiq.commons.exceptions.ApplicationException;
import com.iotiq.commons.util.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

}
