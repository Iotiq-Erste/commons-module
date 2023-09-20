package com.iotiq.commons.config;

import com.iotiq.commons.util.MessageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.FieldError;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

    @InjectMocks
    MessageUtil messageUtil;

    @Mock
    ResourceBundleMessageSource messageSource;

    @Test
    void noDefaultMessageInFieldError() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn(null);

        String message = messageUtil.getMessage(fieldError);

        assertThat(message).isEqualTo("resolved message");
    }

    @Test
    void noDefaultMessageInFieldError_getMessageThrowsException() {

    }

    @Test
    void getMessageThrowsException(){

    }

    @Test
    void resolvedMessageEqualsDefaultMessage(){

    }

    @Test
    void resolvedMessageDoesNotEqualDefaultMessage(){
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("resolvedMessage");
        String message = messageUtil.getMessage(new FieldError("objname", "field", "def message"));

        assertThat(message).isEqualTo("resolvedMessage");
    }
}
