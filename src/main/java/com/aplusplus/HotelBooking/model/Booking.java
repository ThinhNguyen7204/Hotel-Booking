package com.aplusplus.HotelBooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Check in date is required")
    private LocalDate checkInDate;
    @NotNull(message = "Check out date is required")
    private LocalDate checkOutDate;
    @Min(value = 0, message = "Number of children must not be less than 0")
    private int numOfChildren;
    @Min(value = 1, message = "Number of adults must not be less than 1")
    private int numOfAdults;
    private int totalNumOfGuest;
    private String bookingCode;
    private Integer percentOfDiscount;
    private Double finalPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
