package com.haya.security;

import com.haya.config.AppProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "unit-test-secret-with-more-than-32-bytes!!".getBytes());
    private static final Instant NOW = Instant.parse("2030-01-01T12:00:00Z");

    private static AppProperties properties(String secret, Duration expiration) {
        return new AppProperties(new AppProperties.Jwt(secret, expiration), null, null, null);
    }

    private static JwtService serviceAt(Instant now) {
        return new JwtService(properties(SECRET, Duration.ofHours(1)), Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void roundTripsTheSubject() {
        JwtService service = serviceAt(NOW);

        JwtService.IssuedToken token = service.issue("alice");

        assertThat(service.verifyAndGetSubject(token.value())).isEqualTo("alice");
        assertThat(token.expiresAt()).isEqualTo(NOW.plus(Duration.ofHours(1)));
    }

    @Test
    void tokensIssuedAtTheSameInstantAreDistinct() {
        JwtService service = serviceAt(NOW);

        assertThat(service.issue("alice").value()).isNotEqualTo(service.issue("alice").value());
    }

    @Test
    void rejectsExpiredTokens() {
        String token = serviceAt(NOW).issue("alice").value();

        assertThatThrownBy(() -> serviceAt(NOW.plus(Duration.ofHours(2))).verifyAndGetSubject(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTokensSignedWithAnotherKey() {
        String otherSecret = Base64.getEncoder().encodeToString("a-completely-different-secret-key-value!!".getBytes());
        String forged = new JwtService(properties(otherSecret, Duration.ofHours(1)), Clock.fixed(NOW, ZoneOffset.UTC))
                .issue("alice").value();

        assertThatThrownBy(() -> serviceAt(NOW).verifyAndGetSubject(forged)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsGarbage() {
        assertThatThrownBy(() -> serviceAt(NOW).verifyAndGetSubject("garbage")).isInstanceOf(JwtException.class);
    }

    @Test
    void refusesToStartWithAWeakSecret() {
        String weak = Base64.getEncoder().encodeToString("too-short".getBytes());

        assertThatThrownBy(() -> new JwtService(properties(weak, Duration.ofHours(1))))
                .isInstanceOf(WeakKeyException.class);
    }
}
