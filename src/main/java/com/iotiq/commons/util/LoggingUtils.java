package com.iotiq.commons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtils {

    public static final String MDC_REQUEST_ID = "traceId";

    public static void info(String var1, Object... var2) {
        log.info(getTracedString(var1), var2);
    }

    public static void error(String var1, Throwable var2) {
        log.error(getTracedString(var1), var2);
    }

    public static void error(String var1) {
        log.error(getTracedString(var1));
    }

    private static String getTracedString(String str) {
        String traceId = getOrCreateRequestId();
        return '-' + traceId + "- " + str;
    }

    private static String getOrCreateRequestId() {
        var requestId = getExistingRequestId();
        if (StringUtils.isEmpty(requestId)) {
            return createRequestId(UUID.randomUUID().toString());
        }
        return requestId;
    }

    public static String createRequestId(String httpRequestId) {
        MDC.put(MDC_REQUEST_ID, httpRequestId);
        return httpRequestId;
    }

    public static String getExistingRequestId() {
        return MDC.get(MDC_REQUEST_ID);
    }
}
