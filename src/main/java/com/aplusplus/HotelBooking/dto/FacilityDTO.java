package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacilityDTO {
    private Long id;
    private Boolean drinkInfo;
    private Boolean gymInfo;
    private Boolean breakfastInfo;
    private Boolean poolInfo;
    private Boolean parkingInfo;
    private Boolean bathInfo;
    private Boolean coffeeInfo;
    private Boolean wifiInfo;
}
