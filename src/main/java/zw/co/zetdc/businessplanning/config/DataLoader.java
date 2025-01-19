package zw.co.zetdc.businessplanning.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import zw.co.zetdc.businessplanning.entities.User;
import zw.co.zetdc.businessplanning.enums.Role;
import zw.co.zetdc.businessplanning.repository.UserRepository;
import zw.co.zetdc.businessplanning.service.impl.AuthenticationServiceImpl;
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        // Create super admin user
        createSuperAdmin();

    }

    private void createSuperAdmin() {
        if (!userRepository.findByEmail("superadmin@zetdc.co.zw").isPresent()) {
            User superAdmin = new User();
            superAdmin.setFirstname("Super");
            superAdmin.setLastname("Admin");
            superAdmin.setEmail("superadmin@zetdc.co.zw");
            superAdmin.setCompanyId(0L);
            superAdmin.setPassword(passwordEncoder.encode("Password@123"));
            superAdmin.setRole(Role.ADMIN);
            superAdmin.setTemporaryPassword(false);

            userRepository.save(superAdmin);
            System.out.println("Default SUPER ADMIN user created.");
        } else {
            System.out.println("SUPER ADMIN user already exists.");
        }
    }

}
