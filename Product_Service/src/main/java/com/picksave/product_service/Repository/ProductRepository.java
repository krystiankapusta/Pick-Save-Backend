package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductNameAndBrand(String productName, String brand);

    @Query("SELECT " +
            "DISTINCT p " +
            "FROM " +
            "Product p " +
            "LEFT JOIN p.categories c " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :phrase, '%'))" +
            "OR " +
            "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :phrase, '%'))")
    List<Product> searchByProductNameOrCategory(@Param("phrase") String phrase);
}
