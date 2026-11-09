
package com.shopsphere.user.controller;

import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.service.CartService;
import com.shopsphere.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(
            CartService cartService,
            UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    // Add product to cart
    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        Long productId =
                Long.valueOf(request.get("productId").toString());

        Integer quantity =
                Integer.valueOf(request.get("quantity").toString());

        CartItemResponse response =
                cartService.addToCart(userId, productId, quantity);

        return ResponseEntity.ok(response);
    }

    // Get logged-in user's cart
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        return ResponseEntity.ok(
                cartService.getCartItems(userId)
        );
    }

    // Remove item from logged-in user's cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        cartService.removeCartItem(userId, itemId);

        return ResponseEntity.ok(
                "Cart item removed successfully"
        );
    }

    // Update quantity of an item in the logged-in user's cart
    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String email = authentication.getName();
        Long userId = userService.getUserByEmail(email).getId();

        Integer quantity =
                Integer.valueOf(request.get("quantity").toString());

        CartItemResponse response =
                cartService.updateCartItemQuantity(
                        userId, itemId, quantity);

        return ResponseEntity.ok(response);
    }
}
