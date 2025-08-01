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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();

    public Category(String categoryName, Set<Product> products) {
        this.categoryName = categoryName;
        this.products = products;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
    public Category(){};

}
