package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.BookingDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IBookingService {
    Response createBooking(Long roomId, String username, Booking request);
    Response getBookingById(String bookingId);
    Response getBookingsByDateAndRoomType(String roomType, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Response getAllBooking(Pageable pageable);
    Response getBookingsByUsername(String username, Pageable pageable);
    Response getBookingsByRoomId(String roomId, Pageable pageable);
    Response updateBooking(BookingDTO request);
    Response cancelBooking(String bookingId);
    Response getRecentBookings(Long userId, LocalDate now, Pageable pageable);
    Response bookingHistory(String username, Pageable pageable);
    Response getBookingsOutOfDue(Pageable pageable);
    Response deleteBookingOutOfDue(String bookingId);
}
