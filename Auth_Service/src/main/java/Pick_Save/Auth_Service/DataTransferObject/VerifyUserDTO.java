package Pick_Save.Auth_Service.DataTransferObject;

import Pick_Save.Auth_Service.Validator.ValidVerificationCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDTO {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    @ValidVerificationCode
    private String verificationCode;
}
