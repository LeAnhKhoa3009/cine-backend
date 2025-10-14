package com.cine.cinemovieservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
    public Integer getTotalSeats() {
        if (roomRow == null || roomColumn == null) {
            return 0;
        }
        char lastRow = Character.toUpperCase(roomRow.trim().charAt(0));
        if (lastRow < 'A' || lastRow > 'O') {
            return 0;
        }
        int totalRows = lastRow - 'A' + 1;
        return totalRows * roomColumn;
    }

    @Transient
    public Set<Seat> generateSeats() {
        Set<Seat> generatedSeats = new LinkedHashSet<>();

        String maxRow = roomRow.trim().toUpperCase();
        char maxRowChar = maxRow.charAt(0);

        for (char rowChar = 'A'; rowChar <= maxRowChar; rowChar++) {
            for (int col = 1; col <= roomColumn; col++) {
                Seat seat = Seat.builder()
                        .seatRow(String.valueOf(rowChar))
                        .seatColumn(col)
                        .seatCode(rowChar + String.valueOf(col))
                        .premium(false)
                        .empty(false) // <-- Change from true to false
                        .room(this)
                        .build();
                generatedSeats.add(seat);
            }
        }

        return generatedSeats;
    }


}




