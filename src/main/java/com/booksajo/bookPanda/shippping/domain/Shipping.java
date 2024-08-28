package com.booksajo.bookPanda.shippping.domain;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.order.dto.OrderRequestDto;
import com.booksajo.bookPanda.shippping.dto.ShippingRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.runner.manipulation.Orderer;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "pay_done_date")
    private LocalDateTime payDoneDate;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @Column(name = "shipping_done_date")
    private LocalDateTime shippingDoneDate;

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;

    @Column(name = "shipping_name", nullable = false)
    private String shippingUserName;

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2", nullable = false)
    private String address2;

    @Column(name = "post_code", nullable = false)
    private String postCode;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", columnDefinition = "BIGINT", nullable = false)
    private Order order;

    @Builder
    public Shipping(ShippingRequestDto requestDto){
        this.order = requestDto.getOrder();
        this.address1 = requestDto.getAddress1();
        this.address2 = requestDto.getAddress2();
        this.postCode = requestDto.getPostCode();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.shippingUserName = requestDto.getShippingUserName();
        this.payDoneDate = requestDto.getPayDoneDate();
        System.out.println("requestDto.getStatus() = " + requestDto.getStatus());
        this.status = requestDto.getStatus();
    }
}
