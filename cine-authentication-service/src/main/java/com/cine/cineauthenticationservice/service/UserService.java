package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.DeactiveUserResponseDTO;
import com.cine.cineauthenticationservice.dto.RetrieveUserReponseDTO;
import com.cine.cineauthenticationservice.dto.SaveUserRequestDTO;
import com.cine.cineauthenticationservice.dto.SaveUserResponseDTO;
import com.cine.cineauthenticationservice.entity.User;

import java.util.List;

public interface UserService {
    User findById(Long id);

    List<User> fetchAllUsers();

    RetrieveUserReponseDTO findByEmail(String email);

    SaveUserResponseDTO saveUser(SaveUserRequestDTO saveUserDTO);

    DeactiveUserResponseDTO deactiveUser(String email);
}
