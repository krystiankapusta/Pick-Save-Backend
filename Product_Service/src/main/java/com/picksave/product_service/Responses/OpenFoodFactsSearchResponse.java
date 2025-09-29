package com.picksave.product_service.Responses;

import lombok.Data;

import java.util.List;

@Data
public class OpenFoodFactsSearchResponse {
    private int count;
    private int page;
    private int page_count;
    private int page_size;
    private int skip;

    private List<OpenFoodFactsProduct> products;
}
