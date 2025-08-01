package com.picksave.product_service.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_name", columnList = "product_name"),
        @Index(name = "idx_product_brand", columnList = "brand"),
        @Index(name = "idx_product_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(nullable = false)
    private String brand;
    @Column(name = "weight_value" ,nullable = false)
    private double weightValue;
    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit", nullable = false)
    private WeightUnit weightUnit;
    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Price> prices = new ArrayList<>();
    @Column(name = "image_url")
    private String imageUrl;
    private String description;
    private String country;
    @Column(name = "production_place")
    private String productionPlace;
    private boolean approved;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product(String productName, String brand, double weightValue, WeightUnit weightUnit, Set<Category> categories,
                   List<Price> prices, String imageUrl, String description, String country, String productionPlace) {
        this.productName = productName;
        this.brand = brand;
        this.weightValue = weightValue;
        this.weightUnit = weightUnit;
        this.categories = categories;
        this.prices = prices;
        this.imageUrl = imageUrl;
        this.description = description;
        this.country = country;
        this.productionPlace = productionPlace;
    }


    public void addPrice(Price price){
        price.setProduct(this);
        this.prices.add(price);
    }
}
