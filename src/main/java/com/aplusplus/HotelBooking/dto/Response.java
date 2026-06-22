package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int statusCode;

    private String message;

    private String token;

    private String role;

    private String expirationTime;

    private String bookingCode;

    private UserDTO user;

    private RoomDTO room;

    private BookingDTO booking;

    private PaymentDTO payment;

    private ReviewDTO review;

    private PromotionDTO promotion;

    private List<UserDTO> userList;

    private List<RoomDTO> roomList;

    private List<BookingDTO> bookingList;

    private List<PaymentDTO> paymentList;

    private List<ReviewDTO> reviewList;

    private List<PromotionDTO> promotionList;

    private ReportDTO report;

    //Pagination information
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
}