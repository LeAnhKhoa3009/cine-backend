package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    RetrieveUserReponseDTO findById(Long id);

    Page<User> fetchAllUsers(Pageable pageable);

    RetrieveUserReponseDTO findByEmail(String email);

    RetrieveUserReponseDTO saveUser(SaveUserRequestDTO saveUserDTO);

    DeactiveUserResponseDTO deactiveUser(String email);

    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);
}
