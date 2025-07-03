package Pick_Save.Product_Service.Responses;

import lombok.Data;

@Data
public class PriceResponse {
    private double amount;
    private String currency;
    private String shop;

    public PriceResponse(double amount, String currency, String shop) {
        this.amount = amount;
        this.currency = currency;
        this.shop = shop;
    }
}
