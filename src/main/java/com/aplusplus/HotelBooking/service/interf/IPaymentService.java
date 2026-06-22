package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.Response;

public interface IPaymentService {
    Response createPayment(String bookingId, String paymentMethod, String paymentStatus, String paymentAmount);
    Response getPaymentById(String paymentId);
    Response getAllPayment();
    Response getPaymentByBookingId(String bookingId); // 1 payment per booking
    Response updatePayment(String paymentId, String paymentMethod, String paymentStatus, String paymentAmount);
    Response deletePayment(String paymentId);
    Response viewPayment();

    Response confirmPayment(Long paymentCode, String paymentStatus);
}
