package com.booksajo.bookPanda.book.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class BookInfo {

    private String title;
    private String link;
    private String image;
    private String author;
    private String discount; //가격.
    private String publisher;
    private String pubdate;
    private String isbn;

    private String description;

}
