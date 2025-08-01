package com.picksave.auth_service.Service;

import com.picksave.auth_service.DataTransferObject.LoginUserDTO;
import com.picksave.auth_service.DataTransferObject.RegisterUserDTO;
import com.picksave.auth_service.DataTransferObject.VerifyUserDTO;
import com.picksave.auth_service.Exceptions.CustomAppException;
import com.picksave.security_common.Model.Role;
import com.picksave.auth_service.Model.User;
import com.picksave.auth_service.Repository.UserRepository;
import com.picksave.auth_service.Responses.ErrorCode;
import com.picksave.auth_service.Responses.RegisterResponse;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public RegisterResponse signup(RegisterUserDTO input)
    {
        if(!isPasswordValid(input.getPassword())){
            throw new CustomAppException(ErrorCode.PASSWORD_INVALID_FORMAT.name(), "Password must be at least 8 characters long, include one uppercase letter, one digit, and one special character.");
        }
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()), Role.USER);
        if (userRepository.existsByEmail(user.getEmail()))
        {
            throw new CustomAppException(ErrorCode.EMAIL_ALREADY_REGISTERED.name(), "Email is already registered");
        }
        if (userRepository.existsByUsername(input.getUsername())){
            throw new CustomAppException(ErrorCode.USERNAME_ALREADY_REGISTERED.name(),"Username is already registered");
        }
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        User savedUser = userRepository.save(user);
        return new RegisterResponse(savedUser.getUsername(), savedUser.getEmail());
    }

    public boolean isPasswordValid(String password){
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return password != null && password.matches(passwordPattern);
    }

    public User authenticate(LoginUserDTO input)
    {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new CustomAppException(ErrorCode.USER_NOT_FOUND.name(), "User not found"));
        if (!user.isEnabled())
        {
            throw new CustomAppException(ErrorCode.EMAIL_NOT_VERIFIED.name(), "Account not verified. Please verify your account");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new CustomAppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD.name(), "Invalid email or password");
        }
        return user;
    }

    public void verifyUser(VerifyUserDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            LocalDateTime expiry = user.getVerificationCodeExpiredAt();
            if(expiry == null || expiry.isBefore(LocalDateTime.now())) {
                throw new CustomAppException(ErrorCode.VERIFICATION_CODE_EXPIRED.name(), "Verification code has expired or not set");
            }
            if(user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiredAt(null);
                userRepository.save(user);
            } else {
                throw new CustomAppException(ErrorCode.INVALID_VERIFICATION_CODE.name(), "Invalid verification code");
            }
        } else {
            throw new CustomAppException(ErrorCode.USER_NOT_FOUND.name(), "User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new CustomAppException(ErrorCode.ACCOUNT_ALREADY_VERIFIED.name(), "Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new CustomAppException(ErrorCode.USER_NOT_FOUND.name(), "User not found");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Pick&Save app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
