package com.booksajo.bookPanda.order.dto;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.order.domain.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {
    private Long id;
    private int quantity;
    private Order order;
    private BookSales bookSales;
}
