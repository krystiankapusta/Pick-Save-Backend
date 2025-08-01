package com.picksave.auth_service.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationCodeDTO {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
}
