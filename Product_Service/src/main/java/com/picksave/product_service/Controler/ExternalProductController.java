package com.picksave.product_service.Controler;

import com.picksave.product_service.Responses.ProductResponse;
import com.picksave.product_service.Service.ExternalProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/external-products/")
public class ExternalProductController {

    private final ExternalProductService externalProductService;
    private static final Logger logger = LoggerFactory.getLogger(ExternalProductController.class);

    public ExternalProductController(ExternalProductService externalProductService) {
        this.externalProductService = externalProductService;
    }

    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    @PostMapping("/fetch/{barcode}")
    public ResponseEntity<?> fetchProduct(@PathVariable String barcode) {
        try {
            logger.info("ExternalProductController got barcode: {}", barcode);
            ProductResponse response = externalProductService.fetchAndSaveProductByBarcode(barcode);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to fetch product from OpenFoodFacts api: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
