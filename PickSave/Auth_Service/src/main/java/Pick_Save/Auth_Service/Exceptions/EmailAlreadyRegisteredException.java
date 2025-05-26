package Pick_Save.Auth_Service.Exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}
