package com.shopsphere.payment.service;

import com.shopsphere.order.Order;
import com.shopsphere.order.OrderStatus;
import com.shopsphere.payment.Payment;
import com.shopsphere.payment.PaymentStatus;
import com.shopsphere.payment.dto.PaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponse processPayment(
            Long userId, PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not authorized to pay for this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Payment cannot be processed for a cancelled order");
        }

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException(
                    "A payment already exists for this order");
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setUser(userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")));

        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        // Development simulation only.
        // A real payment gateway must confirm successful payment.
        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setTransactionId(
                "TXN-" + UUID.randomUUID());

        Payment savedPayment = paymentRepository.save(payment);

          // Update order status after successful payment
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

          return convertToResponse(savedPayment);
    }

    public List<PaymentResponse> getPaymentHistory(Long userId) {

        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public PaymentResponse getPaymentByOrder(
            Long userId, Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        if (!payment.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not authorized to view this payment");
        }

        return convertToResponse(payment);
    }

    private PaymentResponse convertToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getCreatedAt()
        );
    }
}
