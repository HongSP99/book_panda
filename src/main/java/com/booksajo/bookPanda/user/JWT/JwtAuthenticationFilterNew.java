package com.booksajo.bookPanda.user.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilterNew extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    public static final String AUTHORIZATION_HEADER = "Authorization";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("cookie");

        System.out.println(bearerToken);

        // accessToken 추출
        String accessTokenPattern = "accessToken=([^;]+)";
        Pattern accessTokenPatternCompiled = Pattern.compile(accessTokenPattern);

        if(bearerToken != null)
        {
            Matcher accessTokenMatcher = accessTokenPatternCompiled.matcher(bearerToken);

            if ( accessTokenMatcher.find()) {
                String accessToken = accessTokenMatcher.group(1);
                System.out.println("Access Token: " + accessToken);

                if (bearerToken != null && jwtTokenProvider.validateToken(accessToken)) {
                    // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Object principal = authentication.getPrincipal();
                    UserDetails userDetails = (UserDetails) principal;
                    System.out.println(userDetails.getUsername());
                }
            } else {
                System.out.println("Access Token not found.");
            }
        }
        filterChain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
