package com.picksave.product_service.Service;

import com.picksave.product_service.DataTransferObject.PriceDTO;
import com.picksave.product_service.DataTransferObject.ProductDTO;
import com.picksave.product_service.Model.Category;
import com.picksave.product_service.Model.Price;
import com.picksave.product_service.Model.Product;
import com.picksave.product_service.Repository.CategoryRepository;
import com.picksave.product_service.Repository.ProductRepository;
import com.picksave.product_service.Responses.CategoryResponse;
import com.picksave.product_service.Responses.PriceResponse;
import com.picksave.product_service.Responses.ProductResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;

    }

    public ProductResponse addProduct(ProductDTO input) {
        if (productRepository.existsByProductNameAndBrand(input.getProductName(), input.getBrand())) {
            throw new RuntimeException("Product already exists with the same name and brand");
        }


        Set<Category> categoryEntities = input.getCategories().stream().map(
                dto -> categoryRepository.findByCategoryName(dto.getCategoryName())
                        .orElseGet(() -> categoryRepository.save(new Category(dto.getCategoryName())))).collect(Collectors.toSet());

        List<Price> priceEntities = input.getPrices().stream().map(
                dto -> {
                    Price price = new Price(dto.getAmount(), dto.getCurrency(), dto.getShop());
                    price.setSource("UserInput");
                    price.setApproved(false);
                    price.setCreatedAt(LocalDateTime.now());
                    price.setUpdatedAt(LocalDateTime.now());
                    return price;
                }).collect(Collectors.toList());




        Product product = new Product(input.getProductName(), input.getBrand(), input.getWeightValue(),
                input.getWeightUnit(), categoryEntities, priceEntities, input.getImageUrl(),
                input.getDescription(), input.getCountry(), input.getProductionPlace());

        product.setApproved(false);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        priceEntities.forEach(price -> price.setProduct(product));
        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts(){

        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public ProductResponse updateProduct(Long id, ProductDTO input) {

        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        logger.info("Prices list before set update time -> {}", input.getPrices());

        Set<Category> categoryEntities = input.getCategories().stream().map(
                dto -> categoryRepository.findByCategoryName(dto.getCategoryName())
                        .orElseGet(() -> categoryRepository.save(new Category(dto.getCategoryName())))).collect(Collectors.toSet());

        Map<String, Price> existingPrices = product.getPrices().stream()
                .collect(Collectors.toMap(
                        p -> p.getShop() + "-" + p.getCurrency(),
                        p -> p
                ));
        logger.info("existingPrices -> {}", existingPrices);
        product.getPrices().clear();

        for (PriceDTO dto : input.getPrices()) {
            String key = dto.getShop() + "-" + dto.getCurrency();
            Price existing = existingPrices.get(key);
            logger.info("Existing -> {}", existing);

            Price newPrice = new Price(dto.getAmount(), dto.getCurrency(), dto.getShop());
            if (existing != null) {
                newPrice.setCreatedAt(existing.getCreatedAt());
                newPrice.setSource(existing.getSource());
            } else {
                newPrice.setCreatedAt(LocalDateTime.now());
                newPrice.setSource("UserInput");
            }
            newPrice.setUpdatedAt(LocalDateTime.now());
            product.addPrice(newPrice);
        }
        logger.info("Prices list after set update time -> {}", input.getPrices());


        product.setProductName(input.getProductName());
        product.setBrand(input.getBrand());
        product.setWeightValue(input.getWeightValue());
        product.setWeightUnit(input.getWeightUnit());
        product.setCategories(categoryEntities);
        product.setImageUrl(input.getImageUrl());
        product.setDescription(input.getDescription());
        product.setCountry(input.getCountry());
        product.setProductionPlace(input.getProductionPlace());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    public ProductResponse getProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow( () -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow( () -> new RuntimeException("Product not found"));
        productRepository.deleteById(id);
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
                product.getProductName(),
                product.getBrand(),
                product.getWeightValue(),
                product.getWeightUnit(),
                categoryResponses,
                priceResponses,
                product.getImageUrl(),
                product.getDescription(),
                product.getCountry(),
                product.getProductionPlace(),
                product.isApproved(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
