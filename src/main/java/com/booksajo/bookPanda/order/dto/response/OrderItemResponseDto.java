package com.booksajo.bookPanda.order.dto.response;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.order.domain.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemResponseDto {
    private Long id;
    private Long bookId;
    private int quantity;
    private String title;
    private String image;
    private int price;

    public OrderItemResponseDto(OrderItem entity){
        this.id = entity.getId();
        this.bookId = entity.getBookSales().getId();
        this.quantity = entity.getQuantity();
        this.title = entity.getBookSales().getBookInfo().getTitle();
        this.image = entity.getBookSales().getBookInfo().getImage();
        this.price = Integer.parseInt(entity.getBookSales().getBookInfo().getDiscount());
    }
}
