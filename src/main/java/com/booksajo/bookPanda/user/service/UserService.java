package com.booksajo.bookPanda.user.service;

import com.booksajo.bookPanda.user.JWT.JwtToken;
import com.booksajo.bookPanda.user.dto.SignUpDto;
import com.booksajo.bookPanda.user.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    JwtToken signIn(String userEmail, String userPassword);

    @Transactional
    UserDto signUp(SignUpDto signUpDto);
}
