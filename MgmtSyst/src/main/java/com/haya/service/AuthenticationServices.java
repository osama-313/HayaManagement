package com.haya.service;

import com.haya.modles.AuthenticationResponse;
import com.haya.modles.Token;
import com.haya.modles.User;
import com.haya.repository.TokenRepository;
import com.haya.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServices {

    private final UserRepository repo;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;

    public AuthenticationServices(UserRepository repo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    public AuthenticationResponse register(User request){

        // check if user already exist. if exist than authenticate the user
        if(repo.findByUserName(request.getUserName()) == new User()) {
            return new AuthenticationResponse(null, "User already exist",
                    0,null,null);
        }

        User user = request;
        String encodedPassword = this.passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        user = repo.save(user);

        String jwt = jwtService.generateToken(user);

        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User regestration was successful",
                user.getId(),user.getFirstName(),user.getLastName());
    }


    public AuthenticationResponse authenticate(User request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )

        );
        User user = repo.findByUserName(request.getUserName());
        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);


        return new AuthenticationResponse(jwt, "User login was successful",
                user.getId(),user.getFirstName(),user.getLastName());

    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }




}
