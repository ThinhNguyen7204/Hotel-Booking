package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.BookingDTO;
import com.aplusplus.HotelBooking.dto.DateRequest;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.implement.BookingService;
import com.aplusplus.HotelBooking.service.interf.IBookingService;
import com.aplusplus.HotelBooking.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final Utils utils;
    private final UserRepo userRepo;
    @PostMapping("create-booking/{room_id}")
    public ResponseEntity<Response> createBooking(@PathVariable("room_id") String room_id,@RequestBody Booking request){
        Response response = bookingService.createBooking(Long.valueOf(room_id), utils.getCurrentUsername(), request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-all-bookings")
    public ResponseEntity<Response> getAllBooking(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "checkInDate"));
        Response response = bookingService.getAllBooking(pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Response> getBooking(@PathVariable("id") String id){
        Response response = bookingService.getBookingById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("get-by-date-type")
    public ResponseEntity<Response> getBookingByDate(@RequestParam(required = false) String roomType,
                                                     @RequestParam(required = false) LocalDate startDate,
                                                     @RequestParam(required = false) LocalDate endDate,
                                                     @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Response response = bookingService.getBookingsByDateAndRoomType(roomType, startDate, endDate, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/booking-history")
    public ResponseEntity<Response> getBookingsByUserId(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = bookingService.bookingHistory(utils.getCurrentUsername(), pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-by-room/{roomId}")
    public ResponseEntity<Response> getBookingByRoomId(@PathVariable("roomId") String roomId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = bookingService.getBookingsByRoomId(roomId, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<Response> cancelBooking(@PathVariable("bookingId") String bookingId){
        Response response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/recent-bookings")
    public ResponseEntity<Response> getRecentBookings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        LocalDate now = LocalDate.now();
        User user = userRepo.findByEmail(utils.getCurrentUsername()).orElseThrow(() -> new OurException("Username not found"));
        Response response = bookingService.getRecentBookings(user.getId(), now, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-by-user-id/{id}")
    public ResponseEntity<Response> getRecentBookingsByUserId(@PathVariable(value = "id") String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        LocalDate now = LocalDate.now();
        Response response = bookingService.getRecentBookings(Long.valueOf(userId), now, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/late-payment")
    public ResponseEntity<Response> getBookingsOutOfDue(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = bookingService.getBookingsOutOfDue(pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete-booking/{id}")
    public ResponseEntity<Response> deleteBooking(@PathVariable(value = "id") String bookingId){
        Response response = bookingService.deleteBookingOutOfDue(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
