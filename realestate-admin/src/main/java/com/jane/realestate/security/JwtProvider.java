package com.jane.realestate.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String username, String role) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + accessTokenExpiration);
        return Jwts.builder()
                .subject(username)
                .claim("role",role)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(getSigningKey())
                .compact();
    }

    public String createRefreshToken(String username) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .claim("type", "refresh")
                .subject(username)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return  getClaims(token).get("role", String.class);
    }

    public String getTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }
}
