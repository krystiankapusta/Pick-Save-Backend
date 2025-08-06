package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    boolean existsByShop(String shop);

}
