package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.PaymentMapper;
import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.model.Payment;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.BookingRepo;
import com.aplusplus.HotelBooking.repository.PaymentRepo;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.interf.IPaymentService;
import com.aplusplus.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private Utils utils;

    @Override
    public Response createPayment(String bookingId, String paymentMethod, String paymentStatus, String paymentAmount) {
        return null;
    }

    @Override
    public Response getPaymentById(String paymentId) {
        return null;
    }

    @Override
    public Response getAllPayment() {
        return null;
    }

    @Override
    public Response getPaymentByBookingId(String bookingId) {
        return null;
    }

    @Override
    public Response updatePayment(String paymentId, String paymentMethod, String paymentStatus, String paymentAmount) {
        return null;
    }

    @Override
    public Response deletePayment(String paymentId) {
        return null;
    }

    @Override
    public Response viewPayment() {
        return null;
    }

    @Override
    public Response confirmPayment(Long paymentCode, String paymentStatus) {
        Response response = new Response();
        try{
            Payment payment = paymentRepo.findByPaymentCode(paymentCode).orElseThrow(() -> new OurException("Payment not found"));
            payment.setPaymentStatus(paymentStatus);
            paymentRepo.save(payment);

            response.setPayment(paymentMapper.paymentToPaymentDTO(payment));
            response.setStatusCode(200);
            response.setMessage("Confirm payment successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
