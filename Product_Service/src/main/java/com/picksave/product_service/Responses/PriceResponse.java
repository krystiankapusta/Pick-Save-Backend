package com.picksave.product_service.Responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PriceResponse {
    private double amount;
    private String currency;
    private String shop;
    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PriceResponse(double amount, String currency, String shop, boolean approved, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.amount = amount;
        this.currency = currency;
        this.shop = shop;
        this.approved = approved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
