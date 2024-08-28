package com.booksajo.bookPanda.book.dto;

import com.booksajo.bookPanda.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSalesOrderResponseDto {
    private String title;
    private String image;
    private String discount;
    private String userAddress1;
    private String userAddress2;
    private String userPostCode;
    private String userName;
    private String userPhoneNumber;
    private int quantity = 1;

    public void setUser(User user) {
        this.userName = user.getName();
        this.userAddress1 = user.getAddress();
        this.userAddress2 = user.getDetailedAddress();
        this.userPostCode = user.getPostCode();
        this.userPhoneNumber = user.getPhoneNumber();
    }
}
