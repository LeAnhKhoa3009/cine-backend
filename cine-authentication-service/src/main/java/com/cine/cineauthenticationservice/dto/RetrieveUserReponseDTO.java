package com.cine.cineauthenticationservice.dto;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RetrieveUserReponseDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private UserRole role;
    private boolean active;
}
