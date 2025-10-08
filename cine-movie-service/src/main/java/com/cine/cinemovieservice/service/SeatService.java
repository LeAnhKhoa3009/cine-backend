package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.UpdateSeatRequestDTO;
import com.cine.cinemovieservice.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatService {

    List<Seat> fetchAllWithRoomId(Long roomId);

    Optional<Seat> getDetails(Long roomId, Long seatId);

    Seat update(UpdateSeatRequestDTO updateSeatRequestDTO);


}
