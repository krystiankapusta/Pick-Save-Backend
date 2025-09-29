package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.Category;
import com.picksave.product_service.Model.ExternalCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalCategoryRepository extends JpaRepository<ExternalCategory, Long> {
    Optional<ExternalCategory> findByCategoryName(String Name);
}
