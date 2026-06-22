package com.aplusplus.HotelBooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRequest {
    @NotNull(message = "Check in date is required")
    private LocalDate checkInDate;
    @NotNull(message = "Check out date is required")
    private LocalDate checkOutDate;
}
