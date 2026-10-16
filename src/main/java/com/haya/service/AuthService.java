package com.haya.service;

import com.haya.dto.AuthDtos;
import com.haya.dto.UserDtos;
import com.haya.entity.Token;
import com.haya.entity.User;
import com.haya.exception.NotFoundException;
import com.haya.repository.TokenRepository;
import com.haya.repository.UserRepository;
import com.haya.security.AppUserDetails;
import com.haya.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository users;
    private final TokenRepository tokens;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       UserRepository users, TokenRepository tokens) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
        this.tokens = tokens;
    }

    @Transactional
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        User user = users.findById(principal.getId()).orElseThrow(() -> new NotFoundException("User", principal.getId()));

        JwtService.IssuedToken issued = jwtService.issue(user.getUserName());
        tokens.save(new Token(issued.value(), issued.expiresAt(), user));
        return new AuthDtos.LoginResponse(issued.value(), issued.expiresAt(), UserDtos.Response.from(user));
    }

    /** Revokes the given token so it can no longer be used, even before it expires. */
    @Transactional
    public void logout(String token) {
        tokens.findByToken(token).ifPresent(stored -> stored.setLoggedOut(true));
    }

    @Transactional(readOnly = true)
    public UserDtos.Response currentUser(AppUserDetails principal) {
        return users.findById(principal.getId())
                .map(UserDtos.Response::from)
                .orElseThrow(() -> new NotFoundException("User", principal.getId()));
    }
}
