package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "seat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Seat extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String seatCode;

    @Column(nullable = false)
    private String seatRow;

    @Column(nullable = false)
    private Integer seatColumn;

    @Column(nullable = false)
    private Boolean premium = false;

    @Column(nullable = false)
    private Boolean empty = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private Room room;
}
