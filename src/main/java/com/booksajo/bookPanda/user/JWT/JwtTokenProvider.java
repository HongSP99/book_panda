package com.booksajo.bookPanda.user.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60;// 한시간

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        System.out.println(secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
            .setSubject(authentication.getName())
            .claim("auth", authorities)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        String refreshToken = Jwts.builder()
            .setSubject(authentication.getName())
            .setExpiration(new Date(now + (86400000L * 24 * 30)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return JwtToken.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get("auth").toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

            List<String> roles = claims.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("ROLE_"))
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.toList());

            if (!roles.isEmpty()) {
                claims.put("auth", roles);
            }

            return claims;
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    public String extractUserEmail(String refreshToken) {
        return parseClaims(refreshToken).getSubject();
    }

}

