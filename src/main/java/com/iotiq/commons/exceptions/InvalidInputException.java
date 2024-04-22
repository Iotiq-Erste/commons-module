package com.iotiq.commons.exceptions;

import org.springframework.http.HttpStatus;

import java.util.LinkedList;

public class InvalidInputException extends ApplicationException {

    public InvalidInputException(String field) {
        super(HttpStatus.BAD_REQUEST, "InvalidInput", new LinkedList<>(), new Object[]{field});
    }
}
