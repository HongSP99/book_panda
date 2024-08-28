package com.booksajo.bookPanda.shippping.dto;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.shippping.domain.Shipping;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShippingResponseDto {
    private String statusLabel;
    private String shippingUserName;
    private String address1;
    private String address2;
    private String postCode;
    private String phoneNumber;
    private Long orderId;

    public ShippingResponseDto(Shipping entity){
        this.statusLabel = entity.getStatus().getlabel();
        this.shippingUserName = entity.getShippingUserName();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.postCode = entity.getPostCode();
        this.phoneNumber = entity.getPhoneNumber();
        this.orderId = entity.getOrder().getId();
    }
}
