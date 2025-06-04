package Pick_Save.Auth_Service.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private long expiredIn;

    public LoginResponse(String token, long expiredIn) {
        this.token = token;
        this.expiredIn = expiredIn;
    }
}
