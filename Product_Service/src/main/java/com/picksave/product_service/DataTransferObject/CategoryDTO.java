package com.picksave.product_service.DataTransferObject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotBlank(message = "Category is required")
    @Size(min = 3, message = "Category name must be at least 3 characters long")
    private String categoryName;
}
