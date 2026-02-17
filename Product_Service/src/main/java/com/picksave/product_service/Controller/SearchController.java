package com.picksave.product_service.Controller;

import com.picksave.product_service.Responses.MessageResponse;
import com.picksave.product_service.Responses.ProductResponse;
import com.picksave.product_service.Service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @GetMapping("/search_terms")
    public ResponseEntity<?> searchProducts(@RequestParam String phrase) {
        try {
            List<ProductResponse> products = searchService.searchProductByPhrase(phrase);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Failed to search products by phrase {}. Error: {}", phrase, e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("No products found"));
        }
    }

}
