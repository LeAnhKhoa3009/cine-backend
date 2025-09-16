package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="session")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session extends BaseEntity {

    @Column(nullable = false)
    private LocalDate sessionDate;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnore
    private Movie movie;

}
