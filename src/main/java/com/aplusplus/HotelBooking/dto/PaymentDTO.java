package com.aplusplus.HotelBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO {
    @NotBlank(message = "Payment status is required")
    private String paymentStatus;
}
