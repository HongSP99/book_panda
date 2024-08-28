package com.booksajo.bookPanda;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTest {

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    public void encodeTest(){
        System.out.println(passwordEncoder.encode("Admin2##"));
    }
}
