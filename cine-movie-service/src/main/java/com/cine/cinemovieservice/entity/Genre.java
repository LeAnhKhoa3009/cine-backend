package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Genre extends BaseEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "icon", length = 100)
    private String icon;
}
