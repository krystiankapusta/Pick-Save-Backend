package Pick_Save.Product_Service.Responses;

import Pick_Save.Product_Service.DataTransferObject.CategoryDTO;
import Pick_Save.Product_Service.DataTransferObject.PriceDTO;
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
    private String weightUnit;
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
