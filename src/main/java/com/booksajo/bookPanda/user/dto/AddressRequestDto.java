package com.booksajo.bookPanda.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDto {
    private String address;
    private String detailedAddress;
    private String postCode;
}
