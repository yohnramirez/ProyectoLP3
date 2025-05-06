package com.backend.project.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtUtil(String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(algorithm).build();
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }

    public String getClaim(DecodedJWT jwt, String claimName) {
        return jwt.getClaim(claimName).asString();
    }

}
