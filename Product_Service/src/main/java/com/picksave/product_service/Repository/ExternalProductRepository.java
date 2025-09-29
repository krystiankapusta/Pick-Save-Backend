package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.ExternalProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ExternalProductRepository extends JpaRepository<ExternalProduct, Long> {
    Optional<ExternalProduct> findByBarcode(String barcode);

}
