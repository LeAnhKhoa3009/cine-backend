package com.cine.cineauthenticationservice.seed;

import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.enumeration.UserRole;
import com.cine.cineauthenticationservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> adminUserOpt = userRepository.findByEmail("admin.user@cine.com");
        if(adminUserOpt.isEmpty()) {
            User adminUser = User.builder()
                    .email("admin.user@cine.com")
                    .role(UserRole.ADMIN)
                    .name("Super User")
                    .phoneNumber("0592573325")
                    .active(true)
                    .password(passwordEncoder.encode("759426rR@"))
                    .deleted(false)
                    .build();

            userRepository.save(adminUser);
        }

        Optional<User> dummyUserOpt = userRepository.findByEmail("dummy.user@cine.com");
        if(dummyUserOpt.isEmpty()) {
            User dummyUser = User.builder()
                    .email("dummy.user@cine.com")
                    .role(UserRole.USER)
                    .name("Dummy User")
                    .phoneNumber("0592573325")
                    .active(true)
                    .password(passwordEncoder.encode("759426rR@"))
                    .deleted(false)
                    .build();

            userRepository.save(dummyUser);
        }
    }
}
