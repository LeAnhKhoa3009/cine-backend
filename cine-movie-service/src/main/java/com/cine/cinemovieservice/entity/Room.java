package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Room extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(nullable = false, length = 100)
    private String roomRow;

    @Column(nullable = false, length = 100)
    private Integer roomColumn;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private Set<Seat> seats = new HashSet<>();

    @Transient
    public int getTotalSeats() {
        if (roomRow == null || roomRow.isEmpty() || roomColumn == null) {
            return 0;
        }
        return roomRow.length() * roomColumn;
    }

    @Transient
    public Set<Seat> generateSeats() {
        Set<Seat> generatedSeats = new HashSet<>();
        if (roomRow == null || roomRow.isEmpty() || roomColumn == null) {
            return generatedSeats;
        }

        int rowIndex = 1;
        for (char rowLetter : roomRow.toCharArray()) {
            for (int col = 1; col <= roomColumn; col++) {
                Seat seat = Seat.builder()
                        .seatRow(String.valueOf(rowLetter))
                        .seatColumn(col)
                        .seatCode(rowLetter + String.valueOf(col))
                        .premium(false)
                        .empty(true)
                        .room(this)
                        .build();
                generatedSeats.add(seat);
            }
            rowIndex++;
        }
        return generatedSeats;
    }
}




