package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.model.Facility;
import com.aplusplus.HotelBooking.model.Room;
import com.aplusplus.HotelBooking.service.implement.RoomService;
import com.google.firebase.database.core.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/{id}")
    public ResponseEntity<Response> getRoom(@PathVariable("id") String id){
        Response response = roomService.getRoom(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/get-all")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllRoom(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = roomService.getAllRoom(pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addRoom(
            @RequestPart(value = "room") Room room,
            @RequestParam(value = "file", required = false) MultipartFile roomPhoto,
            @RequestPart(value = "facility") Facility facility
    ){
        Response response = roomService.addRoom(room, roomPhoto, facility);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(
            @PathVariable("id") String roomId,
            @RequestPart(value = "room") Room room,
            @RequestParam(value = "file", required = false) MultipartFile roomPhoto,
            @RequestPart(value = "facility") Facility facility){

        Response response = roomService.updateRoom(Long.valueOf(roomId), room, roomPhoto, facility);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable("id") String id){
        Response response = roomService.deleteRoom(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-available-rooms")
    public ResponseEntity<Response> getAvailableRooms(@RequestParam LocalDate checkInDate,
                                                      @RequestParam LocalDate checkOutDate,
                                                      @RequestParam(defaultValue = "2") int totalGuest,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "9") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = roomService.getAvailableRoomsByDateAndNumOfGuest(checkInDate, checkOutDate, totalGuest, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/check-available/{roomId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Response> checkAvailable(@RequestParam LocalDate checkInDate,
                                                   @RequestParam LocalDate checkOutDate,
                                                   @RequestParam(defaultValue = "2") int totalGuest,
                                                   @PathVariable(value = "roomId") String roomId){
        Response response = roomService.checkAvailable(checkInDate, checkOutDate, totalGuest, Long.valueOf(roomId));
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
