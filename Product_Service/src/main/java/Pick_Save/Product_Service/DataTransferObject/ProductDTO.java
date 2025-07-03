package Pick_Save.Product_Service.DataTransferObject;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ProductDTO {
    private String productName;
    private String brand;
    private double weightValue;
    private String weightUnit;
    private Set<CategoryDTO> categories = new HashSet<>();
    private List<PriceDTO> prices = new ArrayList<>();
    private String imageUrl;
    private String description;
    private String country;
    private String productionPlace;
}
