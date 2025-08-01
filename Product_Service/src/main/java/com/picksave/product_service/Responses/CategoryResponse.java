package com.picksave.product_service.Responses;

import lombok.Data;

@Data
public class CategoryResponse {
    private String categoryName;

    public CategoryResponse(String categoryName){
        this.categoryName = categoryName;
    }
}
