package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "image")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BaseEntity {

    private String name;

    private long size;

    private String contentType;

    @Column(length = 64)
    private String checksumSha256;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", nullable = false)
    @JsonIgnore
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = true)
    @JsonIgnore
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = true)
    @JsonIgnore
    private ImageFolder folder;

    @Override
    public String toString() {
        return "Image{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", contentType='" + contentType + '\'' +
                ", checksumSha256='" + checksumSha256 + '\'' +
                '}';
    }
}
