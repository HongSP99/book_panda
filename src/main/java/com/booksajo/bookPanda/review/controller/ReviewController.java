package com.booksajo.bookPanda.review.controller;

import com.booksajo.bookPanda.review.dto.ReviewDto;
import com.booksajo.bookPanda.review.dto.ReviewResponseDto;
import com.booksajo.bookPanda.review.entity.Review;
import com.booksajo.bookPanda.review.service.ReviewService;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @GetMapping("review")
    public ResponseEntity<Review> getReview(@RequestParam(name = "id") Long id)
    {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @GetMapping("reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@RequestParam(name = "id") Long id)
    {
        return ResponseEntity.ok(reviewService.getReviews(id));
    }

    @PostMapping("review")
    public ResponseEntity<Review> postReview(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReviewDto dto)
    {
        String userEmail = userDetails.getUsername();
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException("User " + userEmail + " not found"));
        System.out.println("=======================");
        System.out.println(user.getName());
        System.out.println("=======================");
        return ResponseEntity.ok(reviewService.postReview(dto,user));
    }

    @PatchMapping("review")
    public ResponseEntity<Review> patchReview(@RequestParam(name = "id") Long id, @RequestBody ReviewDto dto)
    {
        return ResponseEntity.ok(reviewService.patchReview(id, dto));
    }

    @DeleteMapping("review")
    public void deleteReview(@RequestParam(name = "id") Long id)
    {
        reviewService.deleteReview(id);
    }
}
