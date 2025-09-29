package com.picksave.product_service.Repository;

import com.picksave.product_service.Model.FetchJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FetchJobRepository extends JpaRepository<FetchJob, Long> {
    Optional<FetchJob> findByShopName(String shopName);
}