package com.booksajo.bookPanda.shippping.dto;

import com.booksajo.bookPanda.order.constant.Status;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShippingRequestDto {
    private String statusLabel;
    private LocalDateTime date;
}
