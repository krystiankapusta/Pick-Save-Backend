package Pick_Save.Product_Service.DataTransferObject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PriceDTO {
    @Positive(message = "Amount must be greater than 0")
    private double amount;
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency alphabetic code must be exactly 3 characters")
    private String currency;
    @NotBlank(message = "Shop name is required")
    @Size(min = 3, message = "Shop name must be at least 3 characters long")
    private String shop;
}
