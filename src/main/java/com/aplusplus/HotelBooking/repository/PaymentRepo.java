package com.aplusplus.HotelBooking.repository;

import com.aplusplus.HotelBooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentCode(Long paymentCode);
    @Query(value = "SELECT SUM(final_price) FROM " +
            "bookings b JOIN payments p ON b.id = p.booking_id " +
            "WHERE YEAR(check_in_date) = :year AND MONTH(check_in_date) = :month AND payment_status = 'PAID'", nativeQuery = true)
    Double getRevenueByYearAndMonth(Integer year, Integer month);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM bookings b JOIN payments p ON b.id = p.booking_id WHERE booking_code = :bookingCode AND payment_status = 'PAID')",nativeQuery = true)
    Long isPaid(String bookingCode);
}
