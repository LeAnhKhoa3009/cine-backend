package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image_folder")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFolder extends BaseEntity {

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.DETACH, orphanRemoval = false)
    private List<Image> images = new ArrayList<>();

    @Override
    public String toString() {
        return "ImageFolder{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
