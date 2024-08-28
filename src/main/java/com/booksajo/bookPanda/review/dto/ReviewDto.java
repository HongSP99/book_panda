package com.booksajo.bookPanda.review.dto;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.review.entity.Review;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private BookSales bookSales;
    private Integer rate;
    private String content;

    public Review toEntity()
    {
        return Review.builder()
                .bookSales(this.bookSales)
                .rate(this.rate)
                .content(this.content)
                .build();
    }


}
