package com.iotiq.commons.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class EntityAlreadyExistsException extends ApplicationException {

    public EntityAlreadyExistsException(String entityName, Object... args) {
        super(HttpStatus.CONFLICT, "entityAlreadyExists", List.of(entityName), args);
    }
}
