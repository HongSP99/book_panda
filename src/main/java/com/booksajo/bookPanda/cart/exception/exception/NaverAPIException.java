package com.booksajo.bookPanda.cart.exception.exception;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NaverAPIException extends RuntimeException{
    private final ErrorCode errorCode;
}
