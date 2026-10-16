package com.haya.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(@NotBlank String userName, @NotBlank String password) {
    }

    public record LoginResponse(String token, Instant expiresAt, UserDtos.Response user) {
    }
}
