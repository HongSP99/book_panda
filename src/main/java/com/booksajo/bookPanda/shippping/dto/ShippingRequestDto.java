package com.booksajo.bookPanda.shippping.dto;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingRequestDto {
    private Order order;
    private Status status;
    private String statusLabel;
    private LocalDateTime payDoneDate;
    private String shippingUserName;
    private String address1;
    private String address2;
    private String postCode;
    private String phoneNumber;

    public void setOrder(Order order){
        this.order = order;
        this.status = order.getStatus();
        this.statusLabel = order.getStatus().getlabel();
    }
}
