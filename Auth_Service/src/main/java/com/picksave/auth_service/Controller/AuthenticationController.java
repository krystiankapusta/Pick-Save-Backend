package com.picksave.auth_service.Controller;

import com.picksave.auth_service.DataTransferObject.LoginUserDTO;
import com.picksave.auth_service.DataTransferObject.RegisterUserDTO;
import com.picksave.auth_service.DataTransferObject.ResendVerificationCodeDTO;
import com.picksave.auth_service.DataTransferObject.VerifyUserDTO;
import com.picksave.auth_service.Exceptions.CustomAppException;
import com.picksave.auth_service.Model.User;
import com.picksave.auth_service.Responses.ErrorResponse;
import com.picksave.auth_service.Responses.LoginResponse;
import com.picksave.auth_service.Responses.MessageResponse;
import com.picksave.auth_service.Responses.RegisterResponse;
import com.picksave.auth_service.Service.AuthenticationService;
import com.picksave.security_common.Service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", authenticatedUser.getRole().name());
            extraClaims.put("authorities", authenticatedUser.getRole().getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            String jwtToken = jwtService.generateToken(extraClaims, authenticatedUser.getEmail());
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
