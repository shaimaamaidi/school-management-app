package com.example.backend.service.impl.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION = 30*60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION = 60 * 60 * 1000;

    private static final Map<String, Object> ACCESS_TOKEN_CLAIMS;

    static {
        ACCESS_TOKEN_CLAIMS = new HashMap<>();
        ACCESS_TOKEN_CLAIMS.put("token_type", "access");
    }

    private static final Map<String, Object> REFRESH_TOKEN_CLAIMS;

    static {
        REFRESH_TOKEN_CLAIMS = new HashMap<>();
        REFRESH_TOKEN_CLAIMS.put("token_type", "refresh");
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(ACCESS_TOKEN_CLAIMS);

        if (userDetails instanceof com.example.backend.entity.Admin admin) {
            claims.put("admin_id", admin.getId());
            claims.put("role", admin.getRole().name());
        }

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(REFRESH_TOKEN_CLAIMS);

        if (userDetails instanceof com.example.backend.entity.Admin admin) {
            claims.put("admin_id", admin.getId());
            claims.put("role", admin.getRole().name());
        }

        return buildToken(claims, userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isExpiredToken(token));
    }

    private boolean isExpiredToken(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("token_type");
    }

    public String extractUserRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public boolean isAccessToken(String token) {
        return "access".equalsIgnoreCase(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equalsIgnoreCase(getTokenType(token));
    }


}
