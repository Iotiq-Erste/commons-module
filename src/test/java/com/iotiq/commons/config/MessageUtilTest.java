package com.iotiq.commons.config;

import com.iotiq.commons.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.FieldError;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

    @InjectMocks
    MessageUtil messageUtil;

    @Mock
    ResourceBundleMessageSource messageSource;

    String RESOLVED_MESSAGE = "resolved message";
    String DEFAULT_MESSAGE = "default message";

    @Test
    void noDefaultMessageInFieldError() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(null);
        when(messageSource.getMessage(same(fieldError), any(Locale.class))).thenReturn(RESOLVED_MESSAGE);

        String message = messageUtil.getErrorMessage(fieldError);

        assertThat(message).isEqualTo(RESOLVED_MESSAGE);
    }

    @Test
    void noDefaultMessageInFieldError_getMessageThrowsException() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(null);
        when(messageSource.getMessage(any(), any())).thenThrow(NoSuchMessageException.class);

        String message = messageUtil.getErrorMessage(fieldError);

        assertThat(message).isNull();
    }

    @Test
    void getMessageThrowsException() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(DEFAULT_MESSAGE);
        when(messageSource.getMessage(eq(DEFAULT_MESSAGE), any(), any(Locale.class)))
                .thenThrow(NoSuchMessageException.class);

        String message = messageUtil.getErrorMessage(fieldError);

        assertThat(message).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    void resolvedMessageEqualsDefaultMessage() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(DEFAULT_MESSAGE);
        when(messageSource.getMessage(eq(DEFAULT_MESSAGE), any(), any(Locale.class)))
                .thenReturn(DEFAULT_MESSAGE);
        when(messageSource.getMessage(same(fieldError), any(Locale.class))).thenReturn(RESOLVED_MESSAGE);

        String message = messageUtil.getErrorMessage(fieldError);

        assertThat(message).isEqualTo(RESOLVED_MESSAGE);
    }

    @Test
    void resolvedMessageDoesNotEqualDefaultMessage() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(DEFAULT_MESSAGE);
        when(messageSource.getMessage(eq(DEFAULT_MESSAGE), any(), any(Locale.class)))
                .thenReturn(RESOLVED_MESSAGE);

        String message = messageUtil.getErrorMessage(fieldError);

        assertThat(message).isEqualTo(RESOLVED_MESSAGE);
    }
}
