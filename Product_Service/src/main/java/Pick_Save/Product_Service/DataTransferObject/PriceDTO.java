package Pick_Save.Product_Service.DataTransferObject;

import lombok.Data;

@Data
public class PriceDTO {
    private double amount;
    private String currency;
    private String shop;
}
