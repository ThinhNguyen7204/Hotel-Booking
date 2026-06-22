package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDTO {
    private Long id;
    private Integer percentOfDiscount;
    @NotBlank(message = "Promotion title is required")
    private String promotionTitle;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String promotionPhotoUrl;
    private String[] listRoomTypes;

}
