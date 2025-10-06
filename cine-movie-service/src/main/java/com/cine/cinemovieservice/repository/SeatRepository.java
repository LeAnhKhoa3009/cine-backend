package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Seat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends BaseRepository<Seat, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.room.id = :roomId")
    void deleteByRoomId(Long roomId);
}
