package com.booksajo.bookPanda.review.repository;

import com.booksajo.bookPanda.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookSalesId(Long bookSalesId);
}
