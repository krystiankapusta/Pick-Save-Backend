package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.ExternalPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalPriceRepository extends JpaRepository<ExternalPrice, Long> {
    boolean existsByShop(String shop);
}
