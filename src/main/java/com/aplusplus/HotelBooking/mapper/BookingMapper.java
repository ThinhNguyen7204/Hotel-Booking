package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.BookingDTO;
import com.aplusplus.HotelBooking.model.Booking;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class BookingMapper {
    public abstract BookingDTO toBookingDTO(Booking booking);
    public abstract Booking toBooking(BookingDTO bookingDTO);
    public abstract List<BookingDTO> toBookingDTOList(List<Booking> bookingList);
}
