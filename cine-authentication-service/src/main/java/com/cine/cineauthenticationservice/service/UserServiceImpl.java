package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.entity.MileStoneTier;
import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.enumeration.MileStoneTierCode;
import com.cine.cineauthenticationservice.enumeration.UserRole;
import com.cine.cineauthenticationservice.repository.MileStoneTierRepository;
import com.cine.cineauthenticationservice.repository.UserRepository;
import com.cine.cineauthenticationservice.validator.EmailValidator;
import com.cine.cineauthenticationservice.validator.PasswordValidator;
import com.cine.cineauthenticationservice.validator.PhoneValidator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MileStoneTierRepository mileStoneTierRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MileStoneTierRepository mileStoneTierRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mileStoneTierRepository = mileStoneTierRepository;
    }

    @Override
    public RetrieveUserReponseDTO findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(this::retrieveUserDtoFromUser).orElse(null);
    }

    @Override
    public Page<User> fetchAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public RetrieveUserReponseDTO findByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(this::retrieveUserDtoFromUser).orElse(null);
    }

    @Override
    public RetrieveUserReponseDTO saveUser(SaveUserRequestDTO saveUserRequestDTO) {

        if(saveUserRequestDTO.getTierPoint() == null){
            log.error("Tier Point is required");
            throw new IllegalArgumentException("Tier Point is required");
        }

        String newPassword = saveUserRequestDTO.getPassword();
        boolean newPasswordProvided = !StringUtils.isBlank(newPassword);
        if (saveUserRequestDTO.getId() != null) { //Update case
            validateSaveUserRequestDTO(saveUserRequestDTO, newPasswordProvided);
            Optional<User> optionalUser = userRepository.findById(saveUserRequestDTO.getId());
            if (!optionalUser.isPresent()) {
                log.error("User not found with id {}", saveUserRequestDTO.getId());
                throw new RuntimeException("User not found with id: " + saveUserRequestDTO.getId());
            }

            User existedUser = optionalUser.get();
            if (!saveUserRequestDTO.getEmail().equals(existedUser.getEmail())) {
                log.error("Email cannot be changed");
                throw new RuntimeException("Email cannot be changed");
            }

            if(!newPasswordProvided){
                newPassword = existedUser.getPassword();
            }
        } else {
            validateSaveUserRequestDTO(saveUserRequestDTO, true);
            Optional<User> optionalUser = userRepository.findByEmail(saveUserRequestDTO.getEmail());
            if (optionalUser.isPresent()) { //Create case - verify email not exists
                log.error("This email has already been used");
                throw new RuntimeException("This email has already been used");
            }
            newPassword = passwordEncoder.encode(newPassword);
        }

        User user = userRepository.save(createUserFromDto(saveUserRequestDTO, newPassword));
        return Optional.ofNullable(user).map(this::retrieveUserDtoFromUser).orElse(null);
    }

    @Override
    public DeactiveUserResponseDTO deactiveUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(false);
            userRepository.save(user);
            return DeactiveUserResponseDTO.builder().id(user.getId()).build();
        }
        log.error("User not found with email {}", email);
        throw new RuntimeException("User not found with email " + email);
    }

    @Override
    public RestoreUserResponseDTO restoreUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(true);
            userRepository.save(user);
            return RestoreUserResponseDTO.builder().id(user.getId()).build();
        }
        log.error("User not found with email {}", email);
        throw new RuntimeException("User not found with email " + email);
    }

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        validateSaveUserRequestDTO(registerRequestDTO, true);
        //Verify email not exists
        Optional<User> optionalUser = userRepository.findByEmail(registerRequestDTO.getEmail());
        if (optionalUser.isPresent()) {
            log.error("This email has already been used");
            throw new RuntimeException("This email has already been used");
        }

        //Hash password
        String hashedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        //Assign first tier
        MileStoneTier firstTier = mileStoneTierRepository.findByCode(MileStoneTierCode.BRONZE.name()).orElse(null);

        //Save user
        User user = userRepository.save(User.builder()
                .name(registerRequestDTO.getName())
                .email(registerRequestDTO.getEmail())
                .password(hashedPassword)
                .phoneNumber(registerRequestDTO.getPhoneNumber())
                .role(UserRole.USER)
                .active(true)
                .tierPoint(0L)
                .mileStoneTier(firstTier)
                .build());

        return RegisterResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    private User createUserFromDto(SaveUserRequestDTO saveUserRequestDTO, String encodedPassword) {
        String tierCode = String.valueOf(saveUserRequestDTO.getTierCode());
        if (tierCode == null || tierCode.isEmpty()) {
            tierCode = MileStoneTierCode.BRONZE.name();
        }
        MileStoneTier tier = mileStoneTierRepository.findByCode(tierCode).orElse(null);
        if (tier == null) {
            log.warn("Tier not found for code '{}', defaulting to BRONZE", tierCode);
            tier = mileStoneTierRepository.findByCode(MileStoneTierCode.BRONZE.name()).orElse(null);
        }
        return User.builder()
            .id(saveUserRequestDTO.getId())
            .name(saveUserRequestDTO.getName())
            .email(saveUserRequestDTO.getEmail())
            .password(encodedPassword)
            .phoneNumber(saveUserRequestDTO.getPhoneNumber())
            .role(UserRole.USER)
            .tierPoint(saveUserRequestDTO.getTierPoint())
            .mileStoneTier(tier)
            .active(true)
            .build();
    }

    private RetrieveUserReponseDTO retrieveUserDtoFromUser(User user) {
        return RetrieveUserReponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.isActive())
                .tierPoint(user.getTierPoint())
                .tier(RetrieveUserTierResponseDTO.builder()
                        .code(user.getMileStoneTier().getCode())
                        .name(user.getMileStoneTier().getName())
                        .requiredPoints(user.getMileStoneTier().getRequiredPoints())
                        .build())
                .build();
    }

    private void validateSaveUserRequestDTO(RegisterRequestDTO saveUserRequestDTO, boolean shouldValidatePassword) {
        if (StringUtils.isBlank(saveUserRequestDTO.getName())) {
            log.error("Name is required");
            throw new IllegalArgumentException("Name is required");
        }

        if (StringUtils.isBlank(saveUserRequestDTO.getEmail())) {
            log.error("Email is required");
            throw new IllegalArgumentException("Email is required");
        }

        if (!EmailValidator.isValid(saveUserRequestDTO.getEmail())) {
            log.error("Invalid email format");
            throw new IllegalArgumentException("Invalid email format");
        }

        // Only require password if shouldValidatePassword is true
        if (shouldValidatePassword && StringUtils.isBlank(saveUserRequestDTO.getPassword())) {
            log.error("Password is required");
            throw new IllegalArgumentException("Password is required");
        }

        // Only validate password format if shouldValidatePassword is true
        if (shouldValidatePassword && !PasswordValidator.isValid(saveUserRequestDTO.getPassword())) {
            log.error("Invalid password format");
            throw new RuntimeException("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        if (StringUtils.isBlank(saveUserRequestDTO.getPhoneNumber())) {
            log.error("Phone number is required");
            throw new IllegalArgumentException("Phone number is required");
        }

        if (!PhoneValidator.isValid(saveUserRequestDTO.getPhoneNumber())) {
            log.error("Invalid phone number format");
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
}
