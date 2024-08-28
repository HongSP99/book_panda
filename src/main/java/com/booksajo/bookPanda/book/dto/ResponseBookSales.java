package com.booksajo.bookPanda.book.dto;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ResponseBookSales {
    private BookSales bookSales;
    private List<Review> reviewList;
}
