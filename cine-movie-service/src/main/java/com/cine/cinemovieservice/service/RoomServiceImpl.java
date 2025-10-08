package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateRoomRequestDTO;
import com.cine.cinemovieservice.dto.UpdateRoomRequestDTO;
import com.cine.cinemovieservice.entity.Room;
import com.cine.cinemovieservice.entity.Seat;
import com.cine.cinemovieservice.repository.RoomRepository;
import com.cine.cinemovieservice.repository.SeatRepository;
import com.cine.cinemovieservice.validator.RoomValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final RoomValidator roomValidator;

    public RoomServiceImpl(RoomRepository roomRepository, SeatRepository seatRepository, RoomValidator roomValidator) {
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
        this.roomValidator = roomValidator;
    }

    @Override
    public List<Room> fetchAll() {
        try {
            log.info("Retrieving all rooms");
            return roomRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Room> getDetails(Long id) {
        try {
            log.info("Retrieving room details with id {}", id);
            return roomRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Room save(CreateRoomRequestDTO createRoomRequestDTO) {
        roomValidator.validateRoom(createRoomRequestDTO.getRoomRow(), createRoomRequestDTO.getRoomColumn());
        Room room = createRoomFromDTO(createRoomRequestDTO);
        Set<Seat> seats = room.generateSeats();
        room.setSeats(seats);
        return roomRepository.save(room);
    }


    @Override
    @Transactional
    public Room update(UpdateRoomRequestDTO updateRoomRequestDTO) {

        Optional<Room> optionalRoom = roomRepository.findById(updateRoomRequestDTO.getId());

        if (optionalRoom.isEmpty()) {
            log.error("Room not found with id {}", updateRoomRequestDTO.getId());
            return null;
        }

        roomValidator.validateRoom(updateRoomRequestDTO.getRoomRow(), updateRoomRequestDTO.getRoomColumn());

        Room room = optionalRoom.get();
        boolean layoutChanged = isLayoutChanged(room, updateRoomRequestDTO);
        updateRoomFromDTO(room, updateRoomRequestDTO);

        if (layoutChanged) {
            regenerateSeats(room);
        }
        log.info("Room with id {} updated successfully", room.getId());
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            Optional<Room> optionalRoom = roomRepository.findById(id);

            if (optionalRoom.isEmpty()) {
                log.error("Room not found with id {}", id);
                return;
            }
            Room room = optionalRoom.get();
            room.getSeats().clear();
            room.setDeleted(true);
            roomRepository.save(room);
            log.info("Soft deleted room {} and removed all its seats successfully", id);
        } catch (Exception e) {
            log.error("Error soft deleting room with id {}: {}", id, e.getMessage());
        }
    }

    private Room createRoomFromDTO(CreateRoomRequestDTO createRoomRequestDTO) {
        return Room.builder()
                .roomName(createRoomRequestDTO.getRoomName())
                .roomColumn(createRoomRequestDTO.getRoomColumn())
                .roomRow(createRoomRequestDTO.getRoomRow())
                .build();
    }

    private void updateRoomFromDTO(Room targetRoom, UpdateRoomRequestDTO roomRequestDTO) {
        targetRoom.setRoomName(roomRequestDTO.getRoomName());
        targetRoom.setRoomColumn(roomRequestDTO.getRoomColumn());
        targetRoom.setRoomRow(roomRequestDTO.getRoomRow());
    }

    private boolean isLayoutChanged(Room room, UpdateRoomRequestDTO dto) {
        if (room.getRoomRow() == null || room.getRoomColumn() == null) {
            return true;
        }
        boolean rowChanged = !room.getRoomRow().equals(dto.getRoomRow());
        boolean colChanged = !room.getRoomColumn().equals(dto.getRoomColumn());
        return rowChanged || colChanged;
    }

    private void regenerateSeats(Room room) {
        room.getSeats().clear();
        Set<Seat> newSeats = room.generateSeats();
        room.getSeats().addAll(newSeats);
    }

}
