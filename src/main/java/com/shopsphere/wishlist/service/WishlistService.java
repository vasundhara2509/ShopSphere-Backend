
package com.shopsphere.wishlist.service;

import com.shopsphere.entity.Product;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.repository.WishlistRepository;
import com.shopsphere.user.User;
import com.shopsphere.wishlist.Wishlist;
import com.shopsphere.wishlist.dto.WishlistResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 1. Add product to wishlist
    @Transactional
    public WishlistResponse addToWishlist(Long userId, Long productId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("Product is already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return convertToResponse(savedWishlist);
    }

    // 2. Get all wishlist products
    @Transactional(readOnly = true)
    public List<WishlistResponse> getMyWishlist(Long userId) {

        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 3. Remove product from wishlist
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {

        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() ->
                        new RuntimeException("Wishlist item not found"));

        wishlistRepository.delete(wishlist);
    }

    // 4. Convert entity to response DTO
    private WishlistResponse convertToResponse(Wishlist wishlist) {

        Product product = wishlist.getProduct();

        return new WishlistResponse(
                wishlist.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                wishlist.getCreatedAt()
        );
    }
}
