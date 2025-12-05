package com.example.backend.controller;

import com.example.backend.dto.Authentication.AuthenticationRequest;
import com.example.backend.dto.Authentication.AuthenticationResponse;
import com.example.backend.service.impl.Authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registreAdmin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registreAdmin(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
    @PostMapping("/logOut")
    public ResponseEntity<Void> logOut(HttpServletRequest request){
        authenticationService.logOut(request);
        return ResponseEntity.noContent().build();

    }
}
