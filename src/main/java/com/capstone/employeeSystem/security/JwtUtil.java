package com.capstone.employeeSystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username, String role) {
        ArrayList<String> roles = new ArrayList<>();
        roles.add(role);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", new ArrayList<>(roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token){
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token){
        return getClaims(token).getExpiration().before(new Date());
    }

}