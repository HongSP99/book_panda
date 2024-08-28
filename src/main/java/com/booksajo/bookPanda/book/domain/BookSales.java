package com.booksajo.bookPanda.book.domain;


import com.booksajo.bookPanda.book.dto.BookInfo;
import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.review.entity.Review;
import com.booksajo.bookPanda.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_count",nullable = false)
    private Integer visitCount;

    @Column(name = "sell_count" , nullable = false)
    private Integer sellCount;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "description", column = @Column(name = "description", length = 1000 , columnDefinition = "TEXT"))
    })
    @Column(name = "book_info", nullable = false)
    private BookInfo bookInfo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "book_sales_id")  // 단방향 관계를 위해 JoinColumn 사용
    @Transient
    private List<Review> reviewList;

    @ManyToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;
}
