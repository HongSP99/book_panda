package com.booksajo.bookPanda.user.service;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final RedisService redisService;
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public void logoutToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    clearCookie(cookie.getName(), request, response);
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    clearCookie(cookie.getName(), request, response);
                }
            }
        }
    }

    public void clearCookie(String cookieName, HttpServletRequest request,
        HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 쿠키의 만료시간을 0으로 설정하여 삭제
        cookie.setHttpOnly(true); // HttpOnly 설정
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
