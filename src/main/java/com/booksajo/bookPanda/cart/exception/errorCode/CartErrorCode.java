package com.booksajo.bookPanda.cart.exception.errorCode;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책 정보를 찾을 수 없습니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 카트 ID를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
