package com.booksajo.bookPanda.cart.exception.errorCode;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverAPIErrorCode implements ErrorCode {

    INCORRECT_QUERY_REQ_ERROR(HttpStatus.BAD_REQUEST, "Incorrect query request (잘못된 쿼리요청입니다.)"),
    INVALID_DISPLAY_VALUE_ERROR(HttpStatus.BAD_REQUEST, "Invalid display value (부적절한 display 값입니다.)"),
    INVALID_START_VALUE_ERROR(HttpStatus.BAD_REQUEST, "Invalid start value (부적절한 start 값입니다.)"),
    INVALID_SORT_VALUE_ERROR(HttpStatus.BAD_REQUEST, "Invalid sort value (부적절한 sort값입니다.)"),
    INVALID_SEARCH_API_ERROR(HttpStatus.NOT_FOUND, "Invalid search api (존재하지 않는 검색 api 입니다.)");

    private final HttpStatus httpStatus;
    private final String message;

}
