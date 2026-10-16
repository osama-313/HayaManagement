package com.haya.security;

import com.haya.config.AppProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/** Creates and verifies signed JWT access tokens. */
@Service
public class JwtService {

    /** A freshly signed token together with its expiry. */
    public record IssuedToken(String value, Instant expiresAt) {
    }

    private final SecretKey key;
    private final Duration lifetime;
    private final Clock clock;

    @Autowired
    public JwtService(AppProperties properties) {
        this(properties, Clock.systemUTC());
    }

    JwtService(AppProperties properties, Clock clock) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.jwt().secret()));
        this.lifetime = properties.jwt().expiration();
        this.clock = clock;
    }

    public IssuedToken issue(String username) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(lifetime);
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    /**
     * Verifies signature and expiry and returns the token subject.
     *
     * @throws JwtException if the token is malformed, tampered with or expired
     */
    public String verifyAndGetSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
