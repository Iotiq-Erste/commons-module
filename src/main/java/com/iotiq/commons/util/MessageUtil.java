package com.iotiq.commons.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final Logger log = LoggerFactory.getLogger(MessageUtil.class);
    private final ResourceBundleMessageSource messageSource;

    public static HttpStatus.Series getSeries(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.series() : null;
    }

    public String getErrorMessage(FieldError fieldError) {
        // default message is the code we write in the validation annotations' parenthesis. We want to use that code to
        // resolve the exception message here. But if there is no custom code given we don't want the message to default
        // to the validation annotation's message. Instead, the field error should be resolved using the method
        // messageSource.getMessage(fieldError, getLocale())
        // default message can be null
        String defaultMessage = fieldError.getDefaultMessage();

        try {
            if (defaultMessage != null) {
                String resolvedMessage = messageSource.getMessage(defaultMessage, fieldError.getArguments(), getLocale());
                // Only way to determine if the default message has an accompanying message in the message bundle is
                // to check if the resolved message is different from the default message
                if (!resolvedMessage.equals(defaultMessage)) {
                    return resolvedMessage;
                }
            }
            // if the resolved message is the same as the message code (default message) we can try to resolve the
            // field error itself.
            return messageSource.getMessage(fieldError, getLocale());
            // Since both getMessage methods can throw a NoSuchMessageException we wrap the code in a try-catch block.
            // In case of this exception we default to the default message.
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }
}
