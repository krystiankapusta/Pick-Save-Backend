package com.picksave.product_service.DataTransferObject;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExternalPriceDTO {
    @Positive(message = "Amount must be greater than 0")
    private double amount;
    private String currency;
    private String shop;
}
