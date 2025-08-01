package com.picksave.product_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "price", indexes = {
        @Index(name = "idx_price_product_id", columnList = "product_id"),
        @Index(name = "idx_price_shop", columnList = "shop"),
        @Index(name = " idx_price_created", columnList = "created_at")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private double amount;
    @Column(length = 3)
    private String currency;
    private String shop;
    private String source;
    private boolean approved;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Price(double amount, String currency, String shop) {
        this.amount = amount;
        this.currency = currency;
        this.shop = shop;

    }
}
