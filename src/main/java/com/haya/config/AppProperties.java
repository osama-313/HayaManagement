package com.haya.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

/** Type-safe binding for every {@code app.*} setting. */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @Valid @NotNull Jwt jwt,
        @Valid @DefaultValue Cors cors,
        @DefaultValue Bootstrap bootstrap,
        @DefaultValue DemoData demoData) {

    /**
     * @param secret     Base64-encoded HMAC key of at least 256 bits (generate with {@code openssl rand -base64 48})
     * @param expiration lifetime of an issued access token
     */
    public record Jwt(@NotBlank String secret, @DefaultValue("24h") Duration expiration) {
    }

    /** @param allowedOrigins browser origins that may call the API */
    public record Cors(@DefaultValue("http://localhost:4200") List<String> allowedOrigins) {
    }

    /** Optional first administrator, created on startup when the database has no users yet. */
    public record Bootstrap(String adminUserName, String adminPassword, String adminEmail) {

        public boolean isConfigured() {
            return adminUserName != null && !adminUserName.isBlank()
                    && adminPassword != null && !adminPassword.isBlank();
        }
    }

    /** Seeds a small, realistic data set for demos and local development. */
    public record DemoData(@DefaultValue("false") boolean enabled) {
    }
}
