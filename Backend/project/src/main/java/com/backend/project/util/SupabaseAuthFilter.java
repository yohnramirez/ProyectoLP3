package com.backend.project.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SupabaseAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public SupabaseAuthFilter() {
        Dotenv dotenv = Dotenv.load();
        this.jwtUtil = new JwtUtil(dotenv.get("AUTH_SUPABASE_KEY"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                DecodedJWT jwt = jwtUtil.verifyToken(token);

                // Puedes acceder a los claims del token
                String userId = jwtUtil.getClaim(jwt, "sub"); // "sub" es el UID en Supabase
                String email = jwtUtil.getClaim(jwt, "email");

                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", email);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
