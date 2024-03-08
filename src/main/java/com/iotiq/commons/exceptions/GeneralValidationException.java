package com.iotiq.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;

@Getter
public class GeneralValidationException extends ApplicationException {
    private final List<Object> violations;

    protected GeneralValidationException(List<Object> violations) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "unprocessableEntity", new LinkedList<>(), new Object[]{});
        this.violations = violations;
    }
}
