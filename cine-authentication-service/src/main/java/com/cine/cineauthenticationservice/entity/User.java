package com.cine.cineauthenticationservice.entity;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "[user]")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private Long tierPoint = 0L;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private UserRole role;
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "milestonetier_id")
    private MileStoneTier mileStoneTier;
}
