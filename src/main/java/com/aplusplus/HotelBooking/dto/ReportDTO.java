package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportDTO {
    private List<Long> revenuePerMonth;
    private List<Long> totalBookingsPerMonth;
    private List<String> trendingRooms;
    private List<Long> trendingRoomBookingsPerMonth;
}
