package com.booksajo.bookPanda.review.entity;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_sales_id")
    private BookSales bookSales;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rate;
    private String content;
}
