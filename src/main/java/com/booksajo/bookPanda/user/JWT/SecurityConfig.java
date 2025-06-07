package com.booksajo.bookPanda.user.JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // H2 콘솔사용
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/cart/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/mypage/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/users/change-password").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/users/delete-user").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/users/is-user").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/payment/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/order/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("api/user/orders").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("api/shipping/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilterNew(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
