
package com.shopsphere.wishlist.controller;

import com.shopsphere.user.service.UserService;
import com.shopsphere.wishlist.dto.WishlistResponse;
import com.shopsphere.wishlist.service.WishlistService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(
            WishlistService wishlistService,
            UserService userService) {

        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    // 1. Add product to wishlist
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService.getUserByEmail(email).getId();

        WishlistResponse response =
                wishlistService.addToWishlist(userId, productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get my wishlist
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getMyWishlist(
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService.getUserByEmail(email).getId();

        return ResponseEntity.ok(
                wishlistService.getMyWishlist(userId)
        );
    }

    // 3. Remove product from wishlist
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService.getUserByEmail(email).getId();

        wishlistService.removeFromWishlist(userId, productId);

        return ResponseEntity.noContent().build();
    }
}
