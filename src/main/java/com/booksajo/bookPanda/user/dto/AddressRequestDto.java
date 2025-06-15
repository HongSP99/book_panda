package com.booksajo.bookPanda.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AddressRequestDto {
    private String address;
    private String detailedAddress;
    private String postCode;
}
