package Pick_Save.Product_Service.DataTransferObject;

import Pick_Save.Product_Service.Model.WeightUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ProductDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 3, message = "Product name must be at least 3 characters long")
    private String productName;
    @NotBlank(message = "Brand is required")
    @Size(min = 3, message = "Brand name must be at least 3 characters long")
    private String brand;
    @Positive(message = "Weight must be greater than 0")
    private double weightValue;
    @NotNull(message = "Weight unit is required")
    private WeightUnit weightUnit;
    @Size(min = 1, message = "At least one category is required")
    private Set<@Valid CategoryDTO> categories = new HashSet<>();
    @Valid
    private List<PriceDTO> prices = new ArrayList<>();
    private String imageUrl;
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    private String country;
    private String productionPlace;
}
