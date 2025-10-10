package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateRoomRequestDTO;
import com.cine.cinemovieservice.dto.UpdateRoomRequestDTO;
import com.cine.cinemovieservice.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    List<Room> fetchAll();

    Optional<Room> getDetails(Long id);

    Room save(CreateRoomRequestDTO createRoomRequestDTO);

    Room update(UpdateRoomRequestDTO updateRoomRequestDTO);

    void delete(Long id);


}

