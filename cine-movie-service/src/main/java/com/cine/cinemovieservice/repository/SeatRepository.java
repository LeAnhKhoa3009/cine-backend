package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends BaseRepository<Seat, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.room.id = :roomId")
    void deleteByRoomId(Long roomId);

    List<Seat> findByRoomId(Long roomId);
    Optional<Seat> findByIdAndRoomId(Long seatId, Long roomId);


}
