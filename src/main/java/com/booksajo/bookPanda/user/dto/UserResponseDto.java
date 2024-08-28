package com.booksajo.bookPanda.user.dto;

import com.booksajo.bookPanda.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String userEmail;
    private String name;
    private String address;
    private String detailedAddress;
    private String postCode;
    private String phoneNumber;
    private Boolean resign;

    public UserResponseDto(User user) {
    }


    public static UserResponseDto of(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUserEmail(),
            user.getUsername(),
            user.getAddress(),
            user.getDetailedAddress(),
            user.getPostCode(),
            user.getPhoneNumber(),
            user.getResign()
        );
    }
}
