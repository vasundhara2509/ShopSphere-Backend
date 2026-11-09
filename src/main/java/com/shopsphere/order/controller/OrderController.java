
package com.shopsphere.order.controller;

import com.shopsphere.order.dto.OrderDetailsResponse;
import com.shopsphere.order.dto.OrderResponse;
import com.shopsphere.service.OrderService;
import com.shopsphere.user.service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(
            OrderService orderService,
            UserService userService) {

        this.orderService = orderService;
        this.userService = userService;
    }

    // Place order with selected delivery address
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestParam Long addressId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        OrderResponse response =
                orderService.placeOrder(userId, addressId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all orders of logged-in user
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        List<OrderResponse> orders =
                orderService.getMyOrders(userId);

        return ResponseEntity.ok(orders);
    }

    // Get order details
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        OrderDetailsResponse response =
                orderService.getOrderDetails(userId, orderId);

        return ResponseEntity.ok(response);
    }

    // Cancel order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.ok(
                "Order cancelled successfully");
    }
}
