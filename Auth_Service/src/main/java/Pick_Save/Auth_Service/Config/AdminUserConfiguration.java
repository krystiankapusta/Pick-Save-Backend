package Pick_Save.Auth_Service.Config;

import Pick_Save.Auth_Service.Service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminUserConfiguration {

    @Autowired
    private final StartupService startupService;

    public AdminUserConfiguration(StartupService startupService) {
        this.startupService = startupService;
    }

    @Bean
    public CommandLineRunner runOnStartup() {
        return args -> startupService.initAdminUser();
    }
}
