package com.backend.project.controller;

import com.backend.project.dto.LoginRequestDto;
import com.backend.project.dto.RegisterRequestDto;
import com.backend.project.logic.AuthSupabaseLogic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
        Supabase users logic layer
     */
    private final AuthSupabaseLogic authSupabaseLogic;

    /**
     * Constructor
     * @param authSupabaseLogic Supabase users logic layer
     */
    public AuthController(AuthSupabaseLogic authSupabaseLogic) {
        this.authSupabaseLogic = authSupabaseLogic;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        return this.authSupabaseLogic.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        return this.authSupabaseLogic.login(request.getEmail(), request.getPassword());
    }
}
