package com.backend.project.controller;

import com.backend.project.dto.UpdateUserDto;
import com.backend.project.logic.UserSupabaseLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserSupabaseController {

    private final UserSupabaseLogic userSupabaseLogic;

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateUserDto requestDto,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is required.");
        }

        String token = authHeader.replace("Bearer ", "");

        ResponseEntity<String> response = this.userSupabaseLogic.updateUserMetadata(token, requestDto);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
