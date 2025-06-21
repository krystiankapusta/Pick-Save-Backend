package Pick_Save.Auth_Service.Controller;

import Pick_Save.Auth_Service.DataTransferObject.LoginUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.RegisterUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.ResendVerificationCodeDTO;
import Pick_Save.Auth_Service.DataTransferObject.VerifyUserDTO;
import Pick_Save.Auth_Service.Exceptions.CustomAppException;
import Pick_Save.Auth_Service.Model.User;
import Pick_Save.Auth_Service.Responses.ErrorResponse;
import Pick_Save.Auth_Service.Responses.LoginResponse;
import Pick_Save.Auth_Service.Responses.MessageResponse;
import Pick_Save.Auth_Service.Responses.RegisterResponse;
import Pick_Save.Auth_Service.Service.AuthenticationService;
import Pick_Save.Auth_Service.Service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        try {
            RegisterResponse response = authenticationService.signup(registerUserDTO);
            return ResponseEntity.ok(response);
        } catch (CustomAppException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(),ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDTO);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            logger.info("Login response -> {}", loginResponse);
            return ResponseEntity.ok(loginResponse);
        } catch (CustomAppException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(),ex.getMessage()));
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDTO verifyUserDTO) {
        try {
            authenticationService.verifyUser(verifyUserDTO);
            return ResponseEntity.ok(new MessageResponse("Account verified successfully"));
        } catch (CustomAppException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(), ex.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationCode(@Valid @RequestBody ResendVerificationCodeDTO resendVerificationCodeDTO) {
        try {
            authenticationService.resendVerificationCode(resendVerificationCodeDTO.getEmail());
            return ResponseEntity.ok(new MessageResponse("Verification code send"));
        } catch (CustomAppException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getCode(), ex.getMessage()));
        }
    }
}
