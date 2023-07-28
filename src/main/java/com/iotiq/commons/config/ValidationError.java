package com.iotiq.commons.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ValidationError {
    String field;
    Object rejectedValue;
    String message;
}
