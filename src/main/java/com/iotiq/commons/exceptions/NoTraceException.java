package com.iotiq.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class NoTraceException extends ApplicationException {
    private String logNote;

    protected NoTraceException(HttpStatus status, String prefix, List<String> messageParts, Object[] arguments) {
        super(status, prefix, messageParts, arguments);
    }

    protected NoTraceException(HttpStatus status, String prefix, List<String> messageParts) {
        super(status, prefix, messageParts);
    }

    protected NoTraceException(HttpStatus status, String prefix, Object[] args) {
        super(status, prefix, args);
    }

    protected NoTraceException(HttpStatus status, String prefix, String logNote, Object[] args) {
        super(status, prefix, args);
        this.logNote = logNote;
    }

    protected NoTraceException(HttpStatus httpStatus, String prefix) {
        super(httpStatus, prefix);
    }
}
