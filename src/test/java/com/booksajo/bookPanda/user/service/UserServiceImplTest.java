package com.booksajo.bookPanda.user.service;


import com.booksajo.bookPanda.cart.exception.exception.UserException;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.dto.SignUpDto;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.user.service.UserServiceImpl;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    UserServiceImplTest() {
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입이 정상적으로 작동!")
    void signUp(){
        //given
        String expected = "tmdvy0801@gmail.com";
        SignUpDto signUpDto = SignUpDto.builder()
                .userEmail(expected)
                .userPassword("12345678")
                .name("홍승표")
                .phoneNumber("01012345678")
                .address("기흥구")
                .detailedAddress("관곡로")
                .postCode("12345")
                .build();

        //when
        userService.signUp(signUpDto);

        //then
        List<User> actual = userRepository.findAll();
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getUserEmail()).isEqualTo(expected);
    }

    @DisplayName("이미 가입된 회원이 존재 시 오류 반환")
    @Test
    void signUpByDuplicateUser(){
        //given
        String expected = "tmdvy0801@gmail.com";
        SignUpDto signUpDto = SignUpDto.builder()
                .userEmail(expected)
                .userPassword("12345678")
                .name("홍승표")
                .phoneNumber("01012345678")
                .address("기흥구")
                .detailedAddress("관곡로")
                .postCode("12345")
                .build();
        userRepository.save(new User(expected, "12345678", "홍승표", "01012345678", "기흥구", "관곡로", "12345"));

        //when, then
        assertThatThrownBy(() -> userService.signUp(signUpDto))
                .isInstanceOf(UserException.class);
    }
}