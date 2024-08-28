package com.booksajo.bookPanda.book.dto;


import lombok.Data;

@Data
public class NaverRequestVariableDto {

    String query;
    Integer display;
    Integer start;
    String sort;

}
