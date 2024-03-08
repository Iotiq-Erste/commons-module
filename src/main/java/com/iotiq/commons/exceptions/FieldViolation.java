package com.iotiq.commons.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldViolation {
    private String message;
    private String path;
    private Object rejectedValue;
}
