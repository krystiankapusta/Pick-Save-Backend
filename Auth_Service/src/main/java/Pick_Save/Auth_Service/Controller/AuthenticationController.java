package Pick_Save.Auth_Service.Controller;

import Pick_Save.Auth_Service.DataTransferObject.LoginUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.RegisterUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.ResendVerificationCodeDTO;
import Pick_Save.Auth_Service.DataTransferObject.VerifyUserDTO;
import Pick_Save.Auth_Service.Model.User;
import Pick_Save.Auth_Service.Responses.LoginResponse;
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDTO);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDTO verifyUserDTO) {
        try {
            authenticationService.verifyUser(verifyUserDTO);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationCode(@Valid @RequestBody ResendVerificationCodeDTO resendVerificationCodeDTO) {
        try {
            authenticationService.resendVerificationCode(resendVerificationCodeDTO.getEmail());
            return ResponseEntity.ok("Verification code send");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
