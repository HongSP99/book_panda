package com.booksajo.bookPanda.book.dto;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.user.domain.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookSalesDto {
    private Long id;
    private Integer visitCount;
    private Integer sellCount;
    private Integer stock;
    private Category category;
    private BookInfo bookInfo;
    // private User user;

    public BookSales toEntity(Category category)
    {
        return BookSales.builder()
                .visitCount(this.visitCount)
                .sellCount(this.sellCount)
                .stock(this.stock)
                .category(category)
                .bookInfo(this.bookInfo)
                .build();
    }
}
