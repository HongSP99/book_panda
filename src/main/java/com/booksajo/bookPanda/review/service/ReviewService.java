package com.booksajo.bookPanda.review.service;

import com.booksajo.bookPanda.book.repository.BookSalesRepository;
import com.booksajo.bookPanda.cart.exception.errorCode.ReviewErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.ReviewException;
import com.booksajo.bookPanda.review.dto.ReviewDto;
import com.booksajo.bookPanda.review.dto.ReviewResponseDto;
import com.booksajo.bookPanda.review.entity.Review;
import com.booksajo.bookPanda.review.repository.ReviewRepository;
import com.booksajo.bookPanda.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookSalesRepository bookSalesRepository;

    public Review getReview(Long id)
    {
        return reviewRepository.findById(id).orElseThrow(()->
                new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    @Transactional
    public List<ReviewResponseDto> getReviews(Long id)
    {
        return ReviewResponseDto.convertToDtoList(reviewRepository.findByBookSalesId(id));
    }

    @Transactional
    public Review postReview(ReviewDto dto, User user)
    {
        Review newReview = dto.toEntity();
        //BookSales bookSales = bookSalesRepository.findById(dto.getBookSales().getId()).orElseThrow();
        newReview.setUser(user);
        //newReview.setBookSales(bookSales);
        System.out.println(dto.getBookSales());
        return reviewRepository.save(newReview);
    }

    public Review patchReview(Long id, ReviewDto dto)
    {
        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.setBookSales(dto.getBookSales());
        review.setRate(dto.getRate());
        review.setContent(dto.getContent());

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id)
    {
        reviewRepository.findById(id).orElseThrow(()->
                new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        reviewRepository.deleteById(id);
    }
}
