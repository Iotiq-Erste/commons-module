package com.iotiq.commons.exceptions;

import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * copied from @see org.springframework.util.Assert
 */
public abstract class Assert {
    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new RequiredFieldMissingException(message);
        }
    }

    public static void notNull(@Nullable Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new RequiredFieldMissingException(nullSafeGet(messageSupplier));
        }
    }

    @Nullable
    private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
        return messageSupplier != null ? (String) messageSupplier.get() : null;
    }
}