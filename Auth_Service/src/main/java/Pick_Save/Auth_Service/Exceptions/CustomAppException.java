package Pick_Save.Auth_Service.Exceptions;


import lombok.Getter;

@Getter
public class CustomAppException extends RuntimeException {
    private final String code;
    public CustomAppException(String code, String message){
        super(message);
        this.code = code;
    }
}
