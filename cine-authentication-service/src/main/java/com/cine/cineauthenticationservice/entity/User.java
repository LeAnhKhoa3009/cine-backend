package com.cine.cineauthenticationservice.entity;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "[user]")
@Getter
@Setter
public class User extends BaseEntity{
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private UserRole role;
    @Column(nullable = false)
    private boolean active = true;
}
