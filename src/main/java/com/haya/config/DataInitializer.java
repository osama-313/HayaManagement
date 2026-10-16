package com.haya.config;

import com.haya.entity.Role;
import com.haya.entity.User;
import com.haya.repository.UserRepository;
import com.haya.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Makes sure the built-in roles exist and, on an empty database, creates the first administrator. */
@Component
@Order(1)
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleService roles;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties.Bootstrap bootstrap;

    public DataInitializer(RoleService roles, UserRepository users, PasswordEncoder passwordEncoder,
                           AppProperties properties) {
        this.roles = roles;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.bootstrap = properties.bootstrap();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role admin = roles.require(Role.ADMIN);
        roles.require(Role.USER);

        if (users.count() == 0 && bootstrap.isConfigured()) {
            String userName = bootstrap.adminUserName().trim();
            String email = bootstrap.adminEmail() != null && !bootstrap.adminEmail().isBlank()
                    ? bootstrap.adminEmail().trim()
                    : userName + "@localhost";
            users.save(new User("Admin", "User", userName, email,
                    passwordEncoder.encode(bootstrap.adminPassword()), 30, "Administrator", admin));
            log.info("Created initial administrator '{}'", userName);
        }
    }
}
