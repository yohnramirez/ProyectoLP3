package microservice.auth.controller;

import microservice.auth.dto.LoginRequestDto;
import microservice.auth.dto.TokenResponseDto;
import microservice.auth.dto.UserDto;
import microservice.auth.logic.AuthLogic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthLogic authLogic;

    public AuthController(AuthLogic authLogic) {
        this.authLogic = authLogic;
    }

    /**
     * Register a user
     * @param data data info
     * @return UserDto
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody UserDto data) {
        var response = this.authLogic.registerUser(data);
        return ResponseEntity.ok(response);
    }

    /**
     * Register a user
     * @param data data info
     * @return UserDto
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto data) {
        var response = this.authLogic.loginUser(data);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh Json Web Token
     * @param authHeader authorization header
     * @return TokenResponseDto
     */
    @PostMapping("/refresh-token")
    public TokenResponseDto refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return this.authLogic.refreshToken(authHeader);
    }
}
