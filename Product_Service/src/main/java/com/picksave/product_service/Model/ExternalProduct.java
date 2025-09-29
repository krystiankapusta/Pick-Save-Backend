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
public class ExternalProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    private String brand;
    @Column(name = "weight_value")
    private Double weightValue;
    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit")
    private WeightUnit weightUnit;
    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<ExternalCategory> categories = new HashSet<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExternalPrice> prices = new ArrayList<>();
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
    @Column(name = "product_source", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSource source;
    @Column(name = "barcode", unique = true, nullable = false)
    private String barcode;

    public ExternalProduct(String productName, String brand, Double weightValue, WeightUnit weightUnit, Set<ExternalCategory> categories,
                           List<ExternalPrice> prices, String imageUrl, String description, String country, String productionPlace) {
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


    public void addPrice(ExternalPrice externalPrice){
        externalPrice.setExternalProduct(this); // I am not sure that this will be work
        this.prices.add(externalPrice);
    }
}
