package com.booksajo.bookPanda.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader("X-Naver-Client-Id", "5GuY5cszfH0ANUwzundv")
                .defaultHeader("X-Naver-Client-Secret", "wgmhvh4EoN")
                .build();
    }

}