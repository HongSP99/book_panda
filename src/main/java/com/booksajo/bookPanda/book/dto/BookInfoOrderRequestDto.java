package com.booksajo.bookPanda.book.dto;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.user.domain.User;
import java.time.LocalDateTime;

public class BookInfoOrderRequestDto {
    private String discount;
    private User user;
}
