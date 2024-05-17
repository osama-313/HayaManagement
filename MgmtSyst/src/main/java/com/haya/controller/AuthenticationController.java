package com.haya.controller;


import com.haya.modles.AuthenticationResponse;
import com.haya.modles.User;
import com.haya.service.AuthenticationServices;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://195.35.32.197:4200")
@RestController
@Controller
public class AuthenticationController {

private final AuthenticationServices authService;

    public AuthenticationController(AuthenticationServices authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register (@RequestBody User request){
            return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

}
