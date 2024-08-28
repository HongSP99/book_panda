package com.booksajo.bookPanda.review.dto;


import com.booksajo.bookPanda.book.dto.BookSalesResponseDto;
import com.booksajo.bookPanda.review.entity.Review;
import com.booksajo.bookPanda.user.dto.UserResponseDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private BookSalesResponseDto bookSalesResponseDto;
    private UserResponseDto userResponseDto;
    private Integer rate;
    private String content;

    public static ReviewResponseDto convertToDto(Review review) {
        ReviewResponseDto reviewDto = new ReviewResponseDto();
        UserResponseDto userResponse = UserResponseDto.of(review.getUser());
        BookSalesResponseDto bookResponseDto = BookSalesResponseDto.of(review.getBookSales());
        reviewDto.setId(review.getId());
        reviewDto.setBookSalesResponseDto(bookResponseDto);
        reviewDto.setUserResponseDto(userResponse);
        reviewDto.setRate(review.getRate());
        reviewDto.setContent(review.getContent());

        return reviewDto;
    }

    public static List<ReviewResponseDto> convertToDtoList(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewResponseDto::convertToDto)
                .collect(Collectors.toList());
    }

    public static ReviewResponseDto of(Review review)
    {
        return new ReviewResponseDto(
                review.getId(),
                BookSalesResponseDto.of(review.getBookSales()),
                UserResponseDto.of(review.getUser()),
                review.getRate(),
                review.getContent()
        );
    }
}
