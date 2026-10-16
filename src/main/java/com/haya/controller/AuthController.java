package com.haya.controller;

import com.haya.dto.AuthDtos;
import com.haya.dto.UserDtos;
import com.haya.security.AppUserDetails;
import com.haya.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Exchange credentials for a JWT access token")
    AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke the access token used for this request")
    void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if (authorization.startsWith(BEARER_PREFIX)) {
            auth.logout(authorization.substring(BEARER_PREFIX.length()).trim());
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Profile of the signed-in user")
    UserDtos.Response me(@AuthenticationPrincipal AppUserDetails principal) {
        return auth.currentUser(principal);
    }
}
