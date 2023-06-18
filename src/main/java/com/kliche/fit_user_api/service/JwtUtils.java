package com.kliche.fit_user_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class JwtUtils {
    private static final String SECRET_KEY = "secret-key";
    private static final List<String> activeTokens = new ArrayList<>();

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void logoutUser(String token) {
        if(activeTokens.contains(token)) {
            activeTokens.remove(token);
        }
    }

    public static void addActiveToken(String token) {
        activeTokens.add(token);
    }

    public static boolean isTokenActive(String token) {
        return activeTokens.contains(token);
    }

    public static String extractEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
    }
}
