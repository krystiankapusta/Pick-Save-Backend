package Pick_Save.Auth_Service.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String username;
    private String email;

    public RegisterResponse(String username, String email){
        this.username = username;
        this.email = email;
    }
}
