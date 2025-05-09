package com.iotiq.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalServiceException extends ApplicationException {
    private String detailMessage;
    public ExternalServiceException(String message, Object... args) {
        super(HttpStatus.BAD_GATEWAY, message, args);
    }

    public ExternalServiceException(String message, String detailMessage, Object... args) {
        super(HttpStatus.BAD_GATEWAY, message, args);
        this.detailMessage = detailMessage;
    }

    public ExternalServiceException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}