package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.FacilityDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.RoomDTO;
import com.aplusplus.HotelBooking.model.Facility;
import com.aplusplus.HotelBooking.model.Room;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface IRoomService {
    Response getRoom (String roomId);
    Response getAllRoom(Pageable pageable);
    Response addRoom (
            Room newRoom,
            MultipartFile roomPhoto,
            Facility facility);
    Response updateRoom (
            Long roomId,
            Room updatedRoom,
            MultipartFile roomPhoto,
            Facility facility);
    Response deleteRoom (String roomId);

    Response getAvailableRoomsByDateAndNumOfGuest(LocalDate checkInDate, LocalDate checkoutDate, int numOfGuest, Pageable pageable);

    Response checkAvailable(LocalDate checkInDate, LocalDate checkOutDate, int totalGuest, Long roomId);
}
