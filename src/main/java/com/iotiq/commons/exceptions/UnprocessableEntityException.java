package com.iotiq.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;

@Getter
public class UnprocessableEntityException extends ApplicationException {
    private final List<Object> violations;
    public UnprocessableEntityException(List<Object> violations) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "unprocessableEntity", new LinkedList<>(), new Object[]{});
        this.violations = violations;
    }
}
