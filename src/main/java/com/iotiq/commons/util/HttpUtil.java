package com.iotiq.commons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {
    public static HttpStatus.Series getSeries(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.series() : null;
    }

}
