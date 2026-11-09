
package com.shopsphere.wishlist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistResponse {

    private Long wishlistId;
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private LocalDateTime addedAt;

    public WishlistResponse(
            Long wishlistId,
            Long productId,
            String productName,
            String description,
            BigDecimal price,
            String category,
            String imageUrl,
            LocalDateTime addedAt) {

        this.wishlistId = wishlistId;
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.addedAt = addedAt;
    }

    public Long getWishlistId() {
        return wishlistId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}
