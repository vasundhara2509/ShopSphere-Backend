
package com.shopsphere.review.service;

import com.shopsphere.entity.Product;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.ReviewRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.review.Review;
import com.shopsphere.review.dto.ReviewRequest;
import com.shopsphere.review.dto.ReviewResponse;
import com.shopsphere.user.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 1. Add a product review
    @Transactional
    public ReviewResponse addReview(
            Long userId, Long productId, ReviewRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException(
                    "You have already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    // 2. Get all reviews for a product
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 3. Get reviews submitted by the logged-in user
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long userId) {

        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 4. Delete a review
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    // 5. Convert entity to response DTO
    private ReviewResponse convertToResponse(Review review) {

        Product product = review.getProduct();
        User user = review.getUser();

        return new ReviewResponse(
                review.getId(),
                product.getId(),
                product.getName(),
                user.getId(),
                user.getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
