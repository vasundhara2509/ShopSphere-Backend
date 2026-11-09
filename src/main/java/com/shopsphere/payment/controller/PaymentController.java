
package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.PaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.payment.service.PaymentService;
import com.shopsphere.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(
            PaymentService paymentService,
            UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        PaymentResponse response =
                paymentService.processPayment(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        return ResponseEntity.ok(
                paymentService.getPaymentHistory(userId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        Long userId = userService
                .getUserByEmail(email)
                .getId();

        return ResponseEntity.ok(
                paymentService.getPaymentByOrder(userId, orderId));
    }
}
