package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.RoomDTO;
import com.aplusplus.HotelBooking.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RoomMapper {
    public abstract RoomDTO roomToRoomDTO(Room room);

    public abstract Room roomDTOToRoom(RoomDTO roomDTO);
    public abstract List<RoomDTO> roomListToRoomDTOList(List<Room> roomList);
}
