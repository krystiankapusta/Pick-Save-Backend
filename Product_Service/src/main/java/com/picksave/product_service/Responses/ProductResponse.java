package com.picksave.product_service.Responses;

import com.picksave.product_service.Model.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String productName;
    private String brand;
    private double weightValue;
    private WeightUnit weightUnit;
    private Set<CategoryResponse> categories = new HashSet<>();
    private List<PriceResponse> prices = new ArrayList<>();
    private String imageUrl;
    private String description;
    private String country;
    private String productionPlace;
    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
