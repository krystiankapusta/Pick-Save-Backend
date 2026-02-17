package com.picksave.product_service.Service;

import com.picksave.product_service.Model.Product;
import com.picksave.product_service.Repository.ProductRepository;
import com.picksave.product_service.Responses.CategoryResponse;
import com.picksave.product_service.Responses.PriceResponse;
import com.picksave.product_service.Responses.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> searchProductByPhrase(String phrase){

        List<ProductResponse> products = productRepository.searchByProductNameOrCategory(phrase).stream().map(this::mapToResponse).toList();
        if(products.isEmpty()){
            throw new RuntimeException("No products found");
        }
        return products;
    }
    private ProductResponse mapToResponse(Product product) {
        Set<CategoryResponse> categoryResponses = product.getCategories().stream()
                .map(cat -> new CategoryResponse(cat.getCategoryName()))
                .collect(Collectors.toSet());

        List<PriceResponse> priceResponses = product.getPrices().stream()
                .map(p -> new PriceResponse(p.getAmount(), p.getCurrency(), p.getShop(), p.isApproved(),
                        p.getCreatedAt(),
                        p.getUpdatedAt()))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getBrand(),
                product.getWeightValue(),
                product.getWeightUnit(),
                categoryResponses,
                priceResponses,
                product.getImageUrl(),
                product.getDescription(),
                product.getCountries(),
                product.getProductionPlace(),
                product.isApproved(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }


}
