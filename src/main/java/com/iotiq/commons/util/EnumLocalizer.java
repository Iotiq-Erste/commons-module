package com.iotiq.commons.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class EnumLocalizer {

    private final MessageSource messageSource;

    public String getLocalizedValue(Enum<?> enumValue, String enumKey) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageCode = "enum." + enumKey + "." + enumValue.name();
        return messageSource.getMessage(messageCode, null, locale);
    }

    public String getLocalizedOptionValue(String enumValue, String optionValue, String enumKey) {
        Locale locale = LocaleContextHolder.getLocale();
        String messageCode = "enum." + enumKey + "." + enumValue + "." + optionValue;
        return messageSource.getMessage(messageCode, null, locale);
    }
}
