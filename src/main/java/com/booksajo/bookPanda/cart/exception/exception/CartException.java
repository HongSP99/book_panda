package com.booksajo.bookPanda.cart.exception.exception;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartException extends RuntimeException {
    private final ErrorCode ErrorCode;
}
