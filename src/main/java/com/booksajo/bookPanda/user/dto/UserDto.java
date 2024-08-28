package com.booksajo.bookPanda.user.dto;

import com.booksajo.bookPanda.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String userEmail;
    private String name;
    private String address;
    private String detailedAddress;
    private String postCode;
    private String phoneNumber;
    private Boolean resign;

    static public UserDto toDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .userEmail(user.getUserEmail())
            .name(user.getName())
            .address(user.getAddress())
            .detailedAddress(user.getDetailedAddress())
            .postCode(user.getPostCode())
            .phoneNumber(user.getPhoneNumber())
            .resign(user.getResign())
            .build();
    }

    public User toEntity() {
        return User.builder()
            .id(id)
            .userEmail(userEmail)
            .name(name)
            .address(address)
            .detailedAddress(detailedAddress)
            .postCode(postCode)
            .phoneNumber(phoneNumber)
            .resign(resign)
            .build();
    }
}
