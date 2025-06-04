package Pick_Save.Auth_Service.Exceptions;

public class UsernameAlreadyRegisteredException extends RuntimeException{
    public UsernameAlreadyRegisteredException(String message){
        super(message);
    }
}
