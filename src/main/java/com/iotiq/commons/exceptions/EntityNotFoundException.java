package com.iotiq.commons.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException(Class<?> entityClass, Object... args) {
        super(HttpStatus.NOT_FOUND, "entityNotFound", List.of(entityClass.getSimpleName()), args);
    }
}
