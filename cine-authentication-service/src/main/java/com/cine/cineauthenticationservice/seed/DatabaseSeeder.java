package com.cine.cineauthenticationservice.seed;

import com.cine.cineauthenticationservice.dto.ImportMileStoneTier;
import com.cine.cineauthenticationservice.entity.MileStoneTier;
import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.enumeration.MileStoneTierCode;
import com.cine.cineauthenticationservice.enumeration.UserRole;
import com.cine.cineauthenticationservice.repository.MileStoneTierRepository;
import com.cine.cineauthenticationservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final MileStoneTierRepository mileStoneTierRepository;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, MileStoneTierRepository mileStoneTierRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mileStoneTierRepository = mileStoneTierRepository;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void run(String... args) throws Exception {
        for (ImportMileStoneTier tierDTO : loadMileStoneTier()){
            if(mileStoneTierRepository.findById(tierDTO.getId()).isEmpty()){
                MileStoneTier defaultTier = MileStoneTier.builder()
                        .name(tierDTO.getName())
                        .code(tierDTO.getCode())
                        .requiredPoints(tierDTO.getRequiredPoints())
                        .deleted(false)
                        .build();
                mileStoneTierRepository.save(defaultTier);
            }
        }

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
                    .tierPoint(20000L)
                    .mileStoneTier(mileStoneTierRepository.findByCode(MileStoneTierCode.DIAMOND.name()).orElse(null))
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
                    .tierPoint(20000L)
                    .mileStoneTier(mileStoneTierRepository.findByCode(MileStoneTierCode.BRONZE.name()).orElse(null))
                    .build();

            userRepository.save(dummyUser);
        }


    }

    private List<ImportMileStoneTier> loadMileStoneTier() {
        try (InputStream inputStream = new ClassPathResource("database/init-milestonetier-data.json").getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<ImportMileStoneTier>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load milestone data", e);
        }
    }
}
