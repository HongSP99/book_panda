package com.booksajo.bookPanda.cart.domain;

import com.booksajo.bookPanda.book.domain.BookSales;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booksales_id")
    private BookSales bookSales;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "checked")
    private boolean checked;
}
