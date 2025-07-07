package Pick_Save.Product_Service.Service;

import Pick_Save.Product_Service.DataTransferObject.ProductDTO;
import Pick_Save.Product_Service.Model.Category;
import Pick_Save.Product_Service.Model.Price;
import Pick_Save.Product_Service.Model.Product;
import Pick_Save.Product_Service.Repository.CategoryRepository;
import Pick_Save.Product_Service.Repository.ProductRepository;
import Pick_Save.Product_Service.Responses.CategoryResponse;
import Pick_Save.Product_Service.Responses.PriceResponse;
import Pick_Save.Product_Service.Responses.ProductResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        Set<Category> categoryEntities = input.getCategories().stream().map(
                dto -> categoryRepository.findByCategoryName(dto.getCategoryName())
                        .orElseGet(() -> categoryRepository.save(new Category(dto.getCategoryName())))).collect(Collectors.toSet());

        List<Price> priceEntities = input.getPrices().stream().map(
                dto -> new Price(dto.getAmount(), dto.getCurrency(), dto.getShop())).toList();


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

        Set<Category> categoryEntities = input.getCategories().stream().map(
                dto -> categoryRepository.findByCategoryName(dto.getCategoryName())
                        .orElseGet(() -> categoryRepository.save(new Category(dto.getCategoryName())))).collect(Collectors.toSet());

        product.getPrices().clear();
        input.getPrices().forEach(dto -> {
                    Price price = new Price(dto.getAmount(), dto.getCurrency(), dto.getShop());
                    product.addPrice(price);
                });

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
                .map(p -> new PriceResponse(p.getAmount(), p.getCurrency(), p.getShop()))
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
