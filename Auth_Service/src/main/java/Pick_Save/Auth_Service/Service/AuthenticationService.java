package Pick_Save.Auth_Service.Service;

import Pick_Save.Auth_Service.DataTransferObject.LoginUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.RegisterUserDTO;
import Pick_Save.Auth_Service.DataTransferObject.VerifyUserDTO;
import Pick_Save.Auth_Service.Exceptions.EmailAlreadyRegisteredException;
import Pick_Save.Auth_Service.Exceptions.UsernameAlreadyRegisteredException;
import Pick_Save.Auth_Service.Model.Role;
import Pick_Save.Auth_Service.Model.User;
import Pick_Save.Auth_Service.Repository.UserRepository;
import Pick_Save.Auth_Service.Responses.RegisterResponse;
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
            throw new RuntimeException("Password must be at least 8 characters long, include one uppercase letter, one digit, and one special character.");
        }
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()), Role.USER);
        if (userRepository.existsByEmail(user.getEmail()))
        {
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }
        if (userRepository.existsByUsername(input.getUsername())){
            throw new UsernameAlreadyRegisteredException("Username is already registered");
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
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isEnabled())
        {
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    public void verifyUser(VerifyUserDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getVerificationCodeExpiredAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if(user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiredAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiredAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
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
