package com.picksave.product_service.Responses;

import lombok.Data;


@Data
public class OpenFoodFactsProductResponse {
    private int status;
    private String barcode;
    private OpenFoodFactsProduct product;
}
