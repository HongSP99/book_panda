package com.booksajo.bookPanda.cart.dto;

import com.booksajo.bookPanda.user.domain.User;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOrderResponseDto {
    private Long id;
    private List<CartItemDto> cartItems;
    private int totalPrice;
    private String userName;
    private String userAddress1;
    private String userAddress2;
    private String userPostCode;
    private String userPhoneNumber;

    public void setUser(User user) {
        this.userName = user.getName();
        this.userAddress1 = user.getAddress();
        this.userAddress2 = user.getDetailedAddress();
        this.userPostCode = user.getPostCode();
        this.userPhoneNumber = user.getPhoneNumber();
    }
}
