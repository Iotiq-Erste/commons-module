package com.iotiq.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ApplicationValidationException extends ApplicationException {
    private final List<FieldViolation> violations;

    protected ApplicationValidationException(String messagePart, List<FieldViolation> violations) {
        super(HttpStatus.BAD_REQUEST, "applicationValidationException", List.of(messagePart));
        this.violations = violations;
    }
}
