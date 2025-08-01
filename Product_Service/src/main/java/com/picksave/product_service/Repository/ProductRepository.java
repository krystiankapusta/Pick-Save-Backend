package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductNameAndBrand(String productName, String brand);
}
