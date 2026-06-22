package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {
    private Long id;
    private String roomType;
    private String roomSize;
    private Long roomPrice;
    private String roomDescription;
    private String roomStatus;
//  private String bookingCode;
    private String roomPhotoUrl;
    private int roomCapacity;
    private int roomAmount;
    private FacilityDTO facility;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int remain;
    private Double averageRating;
    private Long numberOfRating;
    private Long numberOfBooking;
    private Long newPrice;
    private Long percentOfDiscount;
}
