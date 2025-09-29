package com.picksave.product_service.DataTransferObject;

import com.picksave.product_service.Model.WeightUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ExternalProductDTO {
    @NotBlank(message = "Product name is required")
    private String productName;
    private String brand;
    private Double weightValue;
    private WeightUnit weightUnit;
    private Set<@Valid ExternalCategoryDTO> categories = new HashSet<>();
    @Valid
    private List<ExternalPriceDTO> prices = new ArrayList<>();
    private String imageUrl;
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    private String country;
    private String productionPlace;
}
