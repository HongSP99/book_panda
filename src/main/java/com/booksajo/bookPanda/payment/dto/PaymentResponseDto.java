package com.booksajo.bookPanda.payment.dto;

import com.booksajo.bookPanda.payment.domain.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {
    private String impUid;
    private String merchantUid;
    private int amount;
    private String status;
    private Long orderId;

    public PaymentResponseDto(Payment payment) {
        this.impUid = payment.getImpUid();
        this.merchantUid = payment.getMerchantUid();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.orderId = payment.getOrder().getId();
    }
}