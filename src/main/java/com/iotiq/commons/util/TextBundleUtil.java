package com.iotiq.commons.util;

import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@Component
public class TextBundleUtil {
    private TextBundleUtil() {
    }

    private static final String BUNDLE_FILE = "i18n.messages";
    private static final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_FILE);

    public static String read(String key) {
        try {
            return bundle.getString(key);
        } catch (RuntimeException e) {
            return key;
        }
    }

    public static String read(String key, String... params) {
        try {
            return MessageFormat.format(read(key), params);
        } catch (RuntimeException e) {
            return key;
        }
    }
}