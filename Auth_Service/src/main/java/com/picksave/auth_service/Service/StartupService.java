package com.picksave.auth_service.Service;

import com.picksave.security_common.Model.Role;
import com.picksave.auth_service.Model.User;
import com.picksave.auth_service.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StartupService implements CommandLineRunner {

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final static Logger logger = LoggerFactory.getLogger(StartupService.class);

    public StartupService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initAdminUser();
    }
    @Transactional
    public void initAdminUser() {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("Admin username/password must be set via environment variables or properties");
        }
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User("admin", adminEmail, passwordEncoder.encode(adminPassword), Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            logger.info("Admin account {} created successful", adminEmail);
        } else {
            logger.info("Admin account already exists");
        }
    }

}
