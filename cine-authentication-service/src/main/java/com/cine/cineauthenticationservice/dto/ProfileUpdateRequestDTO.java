package com.cine.cineauthenticationservice.dto;

import lombok.Getter;

@Getter
public class ProfileUpdateRequestDTO {
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
}
