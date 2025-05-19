package com.backend.project.logic;

import com.backend.project.dto.RegisterRequestDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthSupabaseLogic {

    private static final Logger logger = LoggerFactory.getLogger(AuthSupabaseLogic.class);

    private final RestTemplate restTemplate;

    public AuthSupabaseLogic() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    public ResponseEntity<String> register(RegisterRequestDto requestDto) {
        try {
            String supabaseUrl = System.getProperty("AUTH_SUPABASE_URL", System.getenv("AUTH_SUPABASE_URL"));
            String supabaseKey = System.getProperty("AUTH_SUPABASE_KEY", System.getenv("AUTH_SUPABASE_KEY"));

            logger.info("Register endpoint: {}", supabaseUrl != null ? supabaseUrl + "/auth/v1/signup" : "null");
            logger.info("Supabase key present: {}", supabaseKey != null ? "Yes" : "No");

            if (supabaseUrl == null || supabaseKey == null) {
                logger.error("Missing required environment variables: AUTH_SUPABASE_URL or AUTH_SUPABASE_KEY");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error during registration: Missing environment variables");
            }

            String endpoint = supabaseUrl + "/auth/v1/signup";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", supabaseKey);

            HttpEntity<Map<String, Object>> request = getRequest(requestDto, headers);

            return this.restTemplate.postForEntity(endpoint, request, String.class);

        } catch (HttpClientErrorException ex) {
            logger.error("Supabase registration failed: Status {}, Response {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Supabase registration server error: Status {}, Response {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Error during registration: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + ex.getMessage());
        }
    }

    public ResponseEntity<String> login(String email, String password) {
        try {
            String supabaseUrl = System.getProperty("AUTH_SUPABASE_URL", System.getenv("AUTH_SUPABASE_URL"));
            String supabaseKey = System.getProperty("AUTH_SUPABASE_KEY", System.getenv("AUTH_SUPABASE_KEY"));

            logger.info("Login endpoint: {}", supabaseUrl != null ? supabaseUrl + "/auth/v1/token?grant_type=password" : "null");

            if (supabaseUrl == null || supabaseKey == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error during sign in: Missing environment variables");
            }

            String endpoint = supabaseUrl + "/auth/v1/token?grant_type=password";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", supabaseKey);

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            return this.restTemplate.postForEntity(endpoint, request, String.class);

        } catch (HttpClientErrorException ex) {
            logger.error("Supabase login failed: Status {}, Response {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            logger.error("Supabase login server error: Status {}, Response {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Error during sign in: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during sign in: " + ex.getMessage());
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
