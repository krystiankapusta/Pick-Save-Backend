package com.picksave.product_service.Service;

import com.picksave.product_service.Config.OpenFoodFactsMapper;
import com.picksave.product_service.Model.*;
import com.picksave.product_service.Repository.ExternalCategoryRepository;
import com.picksave.product_service.Repository.ExternalProductRepository;
import com.picksave.product_service.Repository.FetchJobRepository;
import com.picksave.product_service.Responses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ExternalProductService {

    private final OpenFoodFactsClient client;
    private final OpenFoodFactsMapper mapper;
    private final ExternalProductRepository externalProductRepository;
    private final ExternalCategoryRepository externalCategoryRepository;
    private final FetchJobRepository fetchJobRepository;
    private static final Logger logger = LoggerFactory.getLogger(ExternalProductService.class);


    public ExternalProductService(OpenFoodFactsClient client, OpenFoodFactsMapper mapper, ExternalProductRepository externalProductRepository, ExternalCategoryRepository externalCategoryRepository, FetchJobRepository fetchJobRepository) {
        this.client = client;
        this.mapper = mapper;
        this.externalProductRepository = externalProductRepository;
        this.externalCategoryRepository = externalCategoryRepository;
        this.fetchJobRepository = fetchJobRepository;
    }

    public ProductResponse fetchAndSaveProductByBarcode(String barcode) {
        OpenFoodFactsProductResponse response = client.fetchProductByBarcode(barcode).block();
        if(response == null || response.getStatus() == 0 ) {
            throw new RuntimeException("Product not found in OpenFoodFacts");
        }

        ExternalProduct newProduct = mapper.toEntity(response);
        newProduct.setBarcode(barcode);
        List<String> countries = newProduct.getCountries().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .toList();

        boolean sellInPoland = false;
        for(String country: countries) {
            if(country.equals("poland") || country.equals("polska")){
                sellInPoland = true;
                break;
            }
        }

        if ((newProduct.getProductName() == null || newProduct.getProductName().isBlank()
                || newProduct.getBrand() == null || newProduct.getBrand().isBlank() )
                || !sellInPoland){
            logger.warn("Skipping product {} because it has no name, brand or it hasn't sell in Poland", barcode);
            return null;
        }

        Optional<ExternalProduct> existingOptional = externalProductRepository.findByBarcode(barcode);
        if (existingOptional.isEmpty()) {
            Set<ExternalCategory> managedCategories = attachCategories(newProduct);
            newProduct.setCategories(managedCategories);
            externalProductRepository.save(newProduct);
            return mapToResponse(newProduct);
        }

        ExternalProduct existing = existingOptional.get();
        if (newProduct.getProductName() != null && !newProduct.getProductName().isBlank()) {
            existing.setProductName(newProduct.getProductName());
        }
        if (newProduct.getBrand() != null) {
            existing.setBrand(newProduct.getBrand());
        }
        if (newProduct.getImageUrl() != null) {
            existing.setImageUrl(newProduct.getImageUrl());
        }
        if (newProduct.getCountries() != null) {
            existing.setCountries(newProduct.getCountries());
        }

        Set<ExternalCategory> managedCategories = attachCategories(newProduct);
        existing.setCategories(managedCategories);
        existing.setUpdatedAt(LocalDateTime.now());
        externalProductRepository.save(existing);
        return mapToResponse(existing);
    }

    @Async
    public Future<Void> fetchAndSaveProductsByShop(String shop) {
        return CompletableFuture.supplyAsync(() -> {
            doFetch(shop);
            return null;
        });
    }

    public void doFetch(String shop) {
        int page;
        FetchJob job = fetchJobRepository.findByShopName(shop)
                .orElseThrow(() -> new RuntimeException("Job not found for shop: " + shop));
        page = job.getLastPage() + 1;

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info("Worker thread interrupted before page fetch for shop {}", shop);
                break;
            }

            job = fetchJobRepository.findByShopName(shop)
                    .orElseThrow(() -> new RuntimeException("Job disappeared!"));

            if (job.getFetchJobStatus() == FetchJobStatus.STOPPED) {
                logger.info("Detected DB STOPPED for shop {} at page {}", shop, job.getLastPage());
                break;
            }

            OpenFoodFactsSearchResponse searchResponse;
            try {
                searchResponse = client.fetchProductsByShop(shop, page).block(Duration.ofSeconds(10));
            } catch (RuntimeException e) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Interrupted during blocking fetch for shop {}, stopping", shop);
                    break;
                }
                logger.warn("Error fetching page {} for shop {}: {}", page, shop, e.getMessage());
                break;
            }

            if (searchResponse == null || searchResponse.getProducts().isEmpty()) {
                logger.info("No more products for shop {}", shop);
                job.setFetchJobStatus(FetchJobStatus.COMPLETED);
                fetchJobRepository.save(job);
                break;
            }

            for (OpenFoodFactsProduct product : searchResponse.getProducts()) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Interrupted mid-page for shop {}", shop);
                    return;
                }
                try {
                    fetchAndSaveProductByBarcode(product.getCode());
                } catch (Exception e) {
                    logger.warn("Skipping product {} due to error: {}", product.getCode(), e.getMessage());
                }
            }

            job.setLastPage(page);
            fetchJobRepository.save(job);
            page++;
        }

        logger.info("doFetch finished for shop {}", shop);
    }

    private Set<ExternalCategory> attachCategories(ExternalProduct product) {
        return product.getCategories().stream()
                .map(cat ->
                {
                    String rawName = cat.getCategoryName();
                    String normalized = rawName.contains(":")
                            ? rawName.substring(rawName.indexOf(":") + 1)
                            : rawName;
                    String finalNormalized = normalized.toLowerCase().replace("-", " ");
                    return externalCategoryRepository.findByCategoryName(finalNormalized)
                            .orElseGet(() -> externalCategoryRepository.save(new ExternalCategory(finalNormalized)));
                })
                .collect(Collectors.toSet());
    }


    private ProductResponse mapToResponse(ExternalProduct product) {
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
