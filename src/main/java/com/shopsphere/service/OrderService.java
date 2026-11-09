
package com.shopsphere.service;

import com.shopsphere.address.Address;
import com.shopsphere.order.dto.OrderResponse;
import com.shopsphere.cart.Cart;
import com.shopsphere.cart.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.order.Order;
import com.shopsphere.order.OrderItem;
import com.shopsphere.order.OrderStatus;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.order.dto.OrderDetailsResponse;
import com.shopsphere.order.dto.OrderItemResponse;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            AddressRepository addressRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, Long addressId) {

        // Verify that the selected address belongs to this user
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid address. Please select your saved address."));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Validate stock and calculate order total
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getQuantity() == null ||
                    product.getQuantity() < cartItem.getQuantity()) {

                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(
                    BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }

        // Create order and attach selected address
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setAddress(address);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Create order items and update stock
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);

            product.setQuantity(
                    product.getQuantity() - cartItem.getQuantity());

            productRepository.save(product);
        }

        orderItemRepository.saveAll(orderItems);

        // Clear cart after successful order creation
        cartItemRepository.deleteAll(cartItems);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(
            Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not authorized to view this order");
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        List<OrderItemResponse> items = orderItems.stream()
                .map(item -> {
                    BigDecimal price = item.getPriceAtPurchase();

                    BigDecimal totalPrice = price.multiply(
                            BigDecimal.valueOf(item.getQuantity()));

                    return new OrderItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getQuantity(),
                            price,
                            totalPrice
                    );
                })
                .collect(Collectors.toList());

        Address address = order.getAddress();

return new OrderDetailsResponse(
        order.getId(),
        order.getTotalAmount(),
        order.getStatus(),
        order.getCreatedAt(),
        items,
        address != null ? address.getFullName() : null,
        address != null ? address.getPhoneNumber() : null,
        address != null ? address.getAddressLine() : null,
        address != null ? address.getLandmark() : null,
        address != null ? address.getCity() : null,
        address != null ? address.getState() : null,
        address != null ? address.getPostalCode() : null
);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "You are not authorized to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "This order cannot be cancelled");
        }

        // Restore product stock
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : orderItems) {

            Product product = item.getProduct();

            product.setQuantity(
                    product.getQuantity() + item.getQuantity());

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }
}
