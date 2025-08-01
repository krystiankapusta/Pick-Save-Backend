package com.picksave.product_service.Controler;

import com.picksave.product_service.DataTransferObject.ProductDTO;
import com.picksave.product_service.Responses.MessageResponse;
import com.picksave.product_service.Responses.ProductResponse;
import com.picksave.product_service.Service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, Principal principal){
        try {
            logger.info("=== ProductController method called ===");
            logger.info("Authenticated principal: {}", principal);
            logger.info("ProductController contain of productDTO -> {}", productDTO);
            ProductResponse response = productService.addProduct(productDTO);
            logger.info("Returning response: {}", response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating product", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editProduct(@Valid @PathVariable Long id, @RequestBody ProductDTO productDTO){
        try {
            logger.info("ProductController update endpoint contain productDTO -> {}", productDTO);
            ProductResponse response = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/display-all")
    public ResponseEntity<?> displayProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to fetch products list"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> productDetails(@PathVariable Long id){
        try {
            ProductResponse response = productService.getProduct(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to fetch product details"));
        }

    }

    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication auth) {
        try {
            logger.info("Delete requested by: {}", auth.getAuthorities());
            productService.deleteProduct(id);
            return ResponseEntity.ok(new MessageResponse("Product successfully deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Product delete failed"));
        }
    }
}
