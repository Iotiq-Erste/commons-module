package com.iotiq.commons.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        String appVersion = this.getEnv("APP_VERSION");
        String gitCommit = this.getEnv("GIT_COMMIT");
        builder.withDetail("app", new AppInfo(appVersion, gitCommit));
    }

    private String getEnv(String key) {
        return System.getenv(key);
    }

    private record AppInfo(String version, String commitId) {
    }
}
