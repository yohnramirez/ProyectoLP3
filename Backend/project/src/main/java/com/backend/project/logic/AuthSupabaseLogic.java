package com.backend.project.logic;

import com.backend.project.dto.RegisterRequestDto;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthSupabaseLogic {

    private final RestTemplate restTemplate;

    public AuthSupabaseLogic() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> register(RegisterRequestDto requestDto) {
        try {
            Dotenv dot = Dotenv.load();

            String endpoint = dot.get("AUTH_SUPABASE_URL") + "/auth/v1/signup";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", dot.get("AUTH_SUPABASE_KEY"));
            headers.set("Authorization", "Bearer " + dot.get("AUTH_SUPABASE_KEY"));

            HttpEntity<Map<String, Object>> request = getRequest(requestDto, headers);

            return this.restTemplate.postForEntity(endpoint, request, String.class);

        } catch (Exception ex) {
            System.out.println("[register]: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during registration");
        }
    }

    public ResponseEntity<String> login(String email, String password) {
        try {
            Dotenv dot = Dotenv.load();

            String endpoint = dot.get("AUTH_SUPABASE_URL") + "/auth/v1/token?grant_type=password";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apiKey", dot.get("AUTH_SUPABASE_KEY"));

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            return this.restTemplate.postForEntity(endpoint, request, String.class);

        } catch (Exception ex) {
            System.out.println("[login]: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during sign in");
        }
    }

    private static @NotNull HttpEntity<Map<String, Object>> getRequest(RegisterRequestDto requestDto, HttpHeaders headers) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", requestDto.getEmail());
        payload.put("password", requestDto.getPassword());

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("fullName", requestDto.getFullName());
        metaData.put("address", requestDto.getAddress());
        metaData.put("birthday", requestDto.getBirthday());
        metaData.put("country", requestDto.getCountry());
        metaData.put("gender", requestDto.getGender());
        metaData.put("typeDocument", requestDto.getTypeDocument());
        metaData.put("numberDocument", requestDto.getNumberDocument());

        payload.put("data", metaData);

        return new HttpEntity<>(payload, headers);
    }
}
