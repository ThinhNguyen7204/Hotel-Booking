package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.PaymentDTO;
import com.aplusplus.HotelBooking.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {
    public abstract PaymentDTO paymentToPaymentDTO(Payment payment);
}
