package com.backend.project.logic;

import com.backend.project.dto.UpdateUserDto;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserSupabaseLogic {

    private final RestTemplate restTemplate;

    public UserSupabaseLogic() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> updateUserMetadata(String accessToken, UpdateUserDto requestDto) {
        try {
            Dotenv dot = Dotenv.load();

            String endpoint = dot.get("AUTH_SUPABASE_URL") + "/auth/v1/user";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("apikey", dot.get("AUTH_SUPABASE_KEY"));

            HttpEntity<Map<String, Object>> request = getRequest(requestDto, headers);

            return restTemplate.exchange(endpoint, HttpMethod.PUT, request, String.class);

        } catch (Exception e) {
            System.out.println("[updateUserMetadata]: " + e.getMessage());
        }

        return null;
    }

    private static @NotNull HttpEntity<Map<String, Object>> getRequest(UpdateUserDto requestDto, HttpHeaders headers) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("address", requestDto.getAddress());
        metadata.put("birthday", requestDto.getBirthday());
        metadata.put("country", requestDto.getCountry());
        metadata.put("typeDocument", requestDto.getTypeDocument());
        metadata.put("numberDocument", requestDto.getNumberDocument());

        Map<String, Object> body = new HashMap<>();
        body.put("data", metadata);

        return new HttpEntity<>(body, headers);
    }
}
