
package com.shopsphere.review.controller;

import com.shopsphere.review.dto.ReviewRequest;
import com.shopsphere.review.dto.ReviewResponse;
import com.shopsphere.review.service.ReviewService;
import com.shopsphere.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(
            ReviewService reviewService,
            UserService userService) {

        this.reviewService = reviewService;
        this.userService = userService;
    }

    // 1. Add a product review
    @PostMapping("/{productId}")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        ReviewResponse response =
                reviewService.addReview(userId, productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all reviews for a product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(productId)
        );
    }

    // 3. Get my reviews
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        return ResponseEntity.ok(
                reviewService.getMyReviews(userId)
        );
    }

    // 4. Delete my review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }
}
