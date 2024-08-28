package com.booksajo.bookPanda.cart.exception.exception;

import com.booksajo.bookPanda.cart.exception.errorCode.UserErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final UserErrorCode userErrorCode;
}
