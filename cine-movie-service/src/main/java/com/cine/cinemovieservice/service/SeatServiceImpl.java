package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.UpdateSeatRequestDTO;
import com.cine.cinemovieservice.entity.Seat;
import com.cine.cinemovieservice.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }


    @Override
    public List<Seat> fetchAllByRoomId(Long roomId) {
        try {
            log.info("Retrieving all seats with room id {}", roomId);
            return seatRepository.findByRoomId(roomId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Seat> fetchBySeatIdAndRoomId(Long roomId, Long seatId) {
        try {
            log.info("Retrieving seat with id {} of room id {}", seatId, roomId);
            return seatRepository.findByIdAndRoomId(seatId, roomId);
        } catch (Exception e) {
            log.error("Error retrieving seat {} of room {}: {}", seatId, roomId, e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public Seat update(UpdateSeatRequestDTO updateSeatRequestDTO) {
        Optional<Seat> optionalSeat = seatRepository.findById(updateSeatRequestDTO.getId());
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            updateSeatFromDTO(seat, updateSeatRequestDTO);
            return seatRepository.save(seat);
        }
        log.error("Seat not found with id {}", updateSeatRequestDTO.getId());
        return null;
    }

    private void updateSeatFromDTO(Seat targetSeat, UpdateSeatRequestDTO updateSeatRequestDTO) {
        targetSeat.setEmpty(updateSeatRequestDTO.getEmpty());
        targetSeat.setPremium(updateSeatRequestDTO.getPremium());
    }

}
