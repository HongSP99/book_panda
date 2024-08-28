package com.booksajo.bookPanda.order.dto.response;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private LocalDateTime orderDate;
    private int totalPrice;
    private String statusLabel;

    public OrderResponseDto(Order entity){
        this.id = entity.getId();
        this.orderDate = entity.getOrderDate();
        this.totalPrice = entity.getTotalPrice();
        this.statusLabel = entity.getStatus().getlabel();
    }

    @Override
    public String toString() {
        return "OrderResponseDto{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", status=" + statusLabel +
                '}';
    }
}
