package com.haya.service;

import com.haya.modles.AuthenticationResponse;
import com.haya.modles.User;
import com.haya.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServices {

    private final UserRepository repo;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationServices(UserRepository repo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request){

        User user = request;
        String encodedPassword = this.passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        user = repo.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }


    public AuthenticationResponse authenticate(User request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )

        );
        User user = repo.findByUserName(request.getUserName());
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);

    }

}
