package com.picksave.product_service.Responses;

import lombok.Data;

import java.util.List;

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
}

