
package com.shopsphere.service;

import com.shopsphere.cart.Cart;
import com.shopsphere.cart.CartItem;
import com.shopsphere.cart.dto.CartItemResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.user.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Add product to cart
    @Transactional
    public CartItemResponse addToCart(
            Long userId, Long productId, Integer quantity) {

        validateQuantity(quantity);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (product.getQuantity() == null ||
                product.getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient product stock");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int updatedQuantity = cartItem.getQuantity() + quantity;

        if (updatedQuantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock");
        }

        cartItem.setQuantity(updatedQuantity);

        CartItem savedItem = cartItemRepository.save(cartItem);

        return convertToResponse(savedItem);
    }

    // Get all cart items
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        return cartItemRepository.findByCartId(cart.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Remove an item from the user's cart
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        cartItemRepository.delete(cartItem);
    }

    // Update quantity of a cart item
    @Transactional
    public CartItemResponse updateCartItemQuantity(
            Long userId, Long itemId, Integer quantity) {

        validateQuantity(quantity);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        Product product = cartItem.getProduct();

        if (product.getQuantity() == null ||
                quantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock");
        }

        cartItem.setQuantity(quantity);

        CartItem updatedItem = cartItemRepository.save(cartItem);

        return convertToResponse(updatedItem);
    }

    // Validate quantity
    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }
    }

    // Convert CartItem entity into response DTO
    private CartItemResponse convertToResponse(CartItem item) {

        Product product = item.getProduct();

        BigDecimal totalPrice = product.getPrice().multiply(
                BigDecimal.valueOf(item.getQuantity())
        );

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                totalPrice
        );
    }
}
