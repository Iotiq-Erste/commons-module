package com.iotiq.commons.message.response;


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
