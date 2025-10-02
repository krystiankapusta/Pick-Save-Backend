package com.picksave.product_service.Responses;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class OpenFoodFactsProduct {
    private String code;
    private String product_name;
    private String brands;
    private String quantity; // example: "750 g" -> needs parsing
    private String product_quantity;
    private String product_quantity_unit;
    private List<String> categories_tags;
    private String image_url;
    private String countries;
    private String generic_name;

    @JsonAnySetter
    private Map<String, Object> otherProperties = new HashMap<>();
    public String getLocalizedName(String lang) {
        String key = "product_name_" + lang;
        Object value = otherProperties.get(key);
        if (value instanceof String str && !str.isBlank()) {
            return str;
        }
        return null;
    }

}

