package com.booksajo.bookPanda.cart.exception.errorCode;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"review 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
