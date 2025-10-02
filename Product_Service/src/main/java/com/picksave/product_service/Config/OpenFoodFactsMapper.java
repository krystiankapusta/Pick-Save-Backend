package com.picksave.product_service.Config;

import com.picksave.product_service.Model.ExternalCategory;
import com.picksave.product_service.Model.ExternalProduct;
import com.picksave.product_service.Model.ProductSource;
import com.picksave.product_service.Model.WeightUnit;
import com.picksave.product_service.Responses.OpenFoodFactsProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OpenFoodFactsMapper {

    private static final List<String> preferredLanguages = Arrays.asList("pl", "en");
    private static final Logger logger = LoggerFactory.getLogger(OpenFoodFactsMapper.class);

    public ExternalProduct toEntity(OpenFoodFactsProductResponse response) {
        var apiProduct = response.getProduct();

        ExternalProduct product = new ExternalProduct();
        product.setProductName(extractPreferredName(response));
        product.setBrand(apiProduct.getBrands());
        product.setImageUrl(apiProduct.getImage_url());
        product.setDescription(apiProduct.getGeneric_name());

        List<String> countriesWithPrefix = Arrays.stream(apiProduct.getCountries().split(","))
                .map(String::trim)
                .toList();

        List<String> countries = countriesWithPrefix.stream()
                .map(country -> country.contains(":")
                        ? country.substring(country.indexOf(":") + 1)
                        : country)
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();

        product.setCountries(countries);
        product.setSource(ProductSource.OPEN_FOOD_FACTS);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setBarcode(response.getBarcode());

        if (apiProduct.getQuantity() != null && apiProduct.getProduct_quantity_unit() != null) {
            String[] parts = apiProduct.getQuantity().split(" ");
            try {
                product.setWeightValue(Double.parseDouble(apiProduct.getProduct_quantity()));
                product.setWeightUnit(WeightUnit.valueOf(apiProduct.getProduct_quantity_unit().toLowerCase()));
            } catch (Exception e) {
                logger.info("Failed to set weight value and unit");
                product.setWeightUnit(null);
                product.setWeightValue(null);
            }
        } else if (apiProduct.getQuantity() != null) {
            String qty = apiProduct.getQuantity().trim().toLowerCase();
            try {
                String numberPart = qty.replaceAll("[^0-9.]", ""); // keep only numbers
                String unitPart = qty.replaceAll("[0-9.]", "");    // keep only letters

                if (!numberPart.isEmpty() && !unitPart.isEmpty()) {
                    product.setWeightValue(Double.parseDouble(numberPart));
                    product.setWeightUnit(WeightUnit.valueOf(unitPart.toLowerCase()));
                }
            } catch (Exception e) {
                logger.info("Failed to set weight value and unit");
                product.setWeightUnit(null);
                product.setWeightValue(null);
            }
        } else {
            product.setWeightUnit(null);
            product.setWeightValue(null);
        }

        Set<ExternalCategory> categories = apiProduct.getCategories_tags().stream()
                .map(ExternalCategory::new)
                .collect(Collectors.toSet());
        product.setCategories(categories);

        return product;
    }

    private String extractPreferredName(OpenFoodFactsProductResponse response){
        for(String prefix : preferredLanguages) {
            String name = response.getProduct().getLocalizedName(prefix);
            if(name != null && !name.isBlank()){
                return name;
            }
        }
        return null;
    }
}

