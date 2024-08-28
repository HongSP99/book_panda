package com.booksajo.bookPanda.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDto {

    private int pages;

    private List<BookSalesDto> books;
}
