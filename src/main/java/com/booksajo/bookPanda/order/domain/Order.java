package com.booksajo.bookPanda.order.domain;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.dto.OrderRequestDto;
import com.booksajo.bookPanda.order.dto.response.OrderResponseDto;
import com.booksajo.bookPanda.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status; // 주문상태

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", columnDefinition = "BIGINT", nullable = false)
    private User user;


    @OneToMany(mappedBy="order", cascade= CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(OrderRequestDto requestDto){
        this.orderDate = requestDto.getOrderDate();
        this.totalPrice = requestDto.getTotalPrice();
        this.status = requestDto.getStatus();
        this.user = requestDto.getUser();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", user=" + user +
                ", orderItems=" + orderItems +
                '}';
    }

}
