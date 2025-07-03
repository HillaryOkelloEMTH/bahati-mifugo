package com.emtech.dairyapp.Auth.Utilities;


import com.emtech.dairyapp.Auth.Data.User.UserData;
import com.emtech.dairyapp.Auth.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.jwtExpirationMs}")
    private String expirationTime;

    public Claims getAllClaimsFromToken(String token) {
            return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }


    private void isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        if (expiration.before(new Date())) {
            throw new TokenExpiredException("Token has expired. Please log in again.");
        }
    }


    public String generateToken(UserData user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        return doGenerateToken(claims,user.getUsername());
    }

    // generate token with user details only
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("now", new Date().toInstant().toString());
        return generateRefreshToken(claims, user);
    }

    private String doGenerateToken(Map<String, Object> claims,String username) {
        Long expirationTimeLong = Long.parseLong(expirationTime); //in second
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + 1000 * 60 * 30); // expire after 30 min

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSignInKey())
                .compact();
    }

    // generate refresh token with extra claims
    public String generateRefreshToken(Map<String, Object> extraClaims, User userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getFirstName()+userDetails.getLastName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 72)) // 3 days minutes exp
                .signWith(getSignInKey())
                .compact();
    }


    public void validateToken(String token) {
        try {
            isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired. Please log in again.");
        }
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
