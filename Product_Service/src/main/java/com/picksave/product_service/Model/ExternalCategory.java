package com.picksave.product_service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category", indexes = {
        @Index(name = "idx_category_name", columnList = "category_name")
})
@Getter
@Setter
@AllArgsConstructor
public class ExternalCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category_name", unique = true)
    private String categoryName;
    @ManyToMany(mappedBy = "categories")
    private Set<ExternalProduct> products = new HashSet<>();

    public ExternalCategory(String categoryName, Set<ExternalProduct> products) {
        this.categoryName = categoryName;
        this.products = products;
    }

    public ExternalCategory(String categoryName) {
        this.categoryName = categoryName;
    }
    public ExternalCategory(){};

}
