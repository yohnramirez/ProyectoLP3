package com.backend.project.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class SupabaseAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthFilter.class);
    private final JwtUtil jwtUtil;

    public SupabaseAuthFilter() {
        String supabaseKey = System.getProperty("AUTH_SUPABASE_KEY", System.getenv("AUTH_SUPABASE_KEY"));
        logger.info("SupabaseAuthFilter: AUTH_SUPABASE_KEY present: {}", supabaseKey != null ? "Yes" : "No");

        if (supabaseKey == null) {
            logger.error("Missing required environment variable: AUTH_SUPABASE_KEY");
            throw new IllegalStateException("Missing required environment variable: AUTH_SUPABASE_KEY");
        }

        this.jwtUtil = new JwtUtil(supabaseKey);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");

            try {
                DecodedJWT jwt = jwtUtil.verifyToken(token);

                String userId = jwtUtil.getClaim(jwt, "sub");
                String email = jwtUtil.getClaim(jwt, "email");

                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", email);

            } catch (Exception e) {
                logger.error("JWT verification failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            logger.debug("No Bearer token found in Authorization header");
        }

        filterChain.doFilter(request, response);
    }
}
