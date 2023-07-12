package com.iotiq.commons.message.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessageDto {
    String requestId;
    List<String> messages;
}
