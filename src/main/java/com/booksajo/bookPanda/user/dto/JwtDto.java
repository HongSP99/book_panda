package com.booksajo.bookPanda.user.dto;

    public record JwtDto (
        String accessToken,
        String refreshToken){
    }
