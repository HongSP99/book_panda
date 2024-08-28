package com.booksajo.bookPanda.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDto {
    private Long id;
    private List<CartItemDto> cartItems;
}
