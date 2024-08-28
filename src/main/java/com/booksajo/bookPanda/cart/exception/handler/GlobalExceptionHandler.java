package com.booksajo.bookPanda.cart.exception.handler;

import com.booksajo.bookPanda.cart.exception.ErrorCode;
import com.booksajo.bookPanda.cart.exception.dto.ErrorResponse;
import com.booksajo.bookPanda.cart.exception.exception.BookSalesException;
import com.booksajo.bookPanda.cart.exception.exception.CartException;
import com.booksajo.bookPanda.cart.exception.exception.CategoryException;
import com.booksajo.bookPanda.cart.exception.exception.NaverAPIException;
import com.booksajo.bookPanda.cart.exception.exception.ReviewException;
import com.booksajo.bookPanda.cart.exception.exception.UserException;
import com.booksajo.bookPanda.cart.exception.errorCode.NaverAPIErrorCode;
import com.booksajo.bookPanda.exception.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NaverAPIException.class) // ①
    public ResponseEntity<Object> handleNaverAPIException(NaverAPIException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(BookSalesException.class) // ①
    public ResponseEntity<Object> handleBookSalesException(BookSalesException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(ReviewException.class) // ①
    public ResponseEntity<Object> handleReviewException(ReviewException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }



    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<Object> handleCategoryException(CategoryException e){
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<Object> handleCartException(CartException e) {
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }


    // 예외처리에 관한 http를 보내는 코드
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, errorCode.getMessage()));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 커스텀 예외 생성 및 반환
        throw new NaverAPIException(NaverAPIErrorCode.INCORRECT_QUERY_REQ_ERROR);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleCustomException(UserException ex) {
        ErrorCode errorCode = ex.getUserErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorCode.getMessage());
    }
}
