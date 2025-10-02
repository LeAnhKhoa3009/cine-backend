package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.enumeration.UserRole;
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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        validateSaveUserRequestDTO(saveUserRequestDTO);
        if (saveUserRequestDTO.getId() != null) { //Update case
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
        } else {
            Optional<User> optionalUser = userRepository.findByEmail(saveUserRequestDTO.getEmail());
            if (optionalUser.isPresent()) { //Create case - verify email not exists
                log.error("This email has already been used");
                throw new RuntimeException("This email has already been used");
            }
        }


        User user = userRepository.save(createUserFromDto(saveUserRequestDTO));
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
        validateSaveUserRequestDTO(registerRequestDTO);
        //Verify email not exists
        Optional<User> optionalUser = userRepository.findByEmail(registerRequestDTO.getEmail());
        if (optionalUser.isPresent()) {
            log.error("This email has already been used");
            throw new RuntimeException("This email has already been used");
        }

        //Hash password
        String hashedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        //Save user
        User user = userRepository.save(User.builder()
                .name(registerRequestDTO.getName())
                .email(registerRequestDTO.getEmail())
                .password(hashedPassword)
                .phoneNumber(registerRequestDTO.getPhoneNumber())
                .role(UserRole.USER)
                .active(true)
                .build());

        return RegisterResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    private User createUserFromDto(SaveUserRequestDTO saveUserRequestDTO) {
        return User.builder()
                .id(saveUserRequestDTO.getId())
                .name(saveUserRequestDTO.getName())
                .email(saveUserRequestDTO.getEmail())
                .password(passwordEncoder.encode(saveUserRequestDTO.getPassword()))
                .phoneNumber(saveUserRequestDTO.getPhoneNumber())
                .role(UserRole.USER)
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
                .build();
    }

    private void validateSaveUserRequestDTO(RegisterRequestDTO saveUserRequestDTO) {
        if (StringUtils.isBlank(saveUserRequestDTO.getEmail())) {
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

        if (StringUtils.isBlank(saveUserRequestDTO.getPassword())) {
            log.error("Password is required");
            throw new IllegalArgumentException("Password is required");
        }

        if (!PasswordValidator.isValid(saveUserRequestDTO.getPassword())) {
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
