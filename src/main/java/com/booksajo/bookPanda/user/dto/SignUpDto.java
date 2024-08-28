// SignUpDto.java
package com.booksajo.bookPanda.user.dto;

import com.booksajo.bookPanda.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {

    private String userEmail;
    private String userPassword;
    private String name;
    private String address;
    private String detailedAddress;
    private String postCode;
    private String phoneNumber;
    private List<String> roles;
    private String authCode;
    private Boolean resign = false;

    public User toEntity(String encodedPassword, List<String> roles) {
        return User.builder()
            .userEmail(userEmail)
            .userPassword(encodedPassword)
            .name(name)
            .roles(roles)
            .address(address)
            .detailedAddress(detailedAddress)
            .postCode(postCode)
            .phoneNumber(phoneNumber)
            .roles(roles)
            .resign(resign)
            .build();
    }

}
