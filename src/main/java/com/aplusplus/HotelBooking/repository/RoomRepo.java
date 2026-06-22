package com.aplusplus.HotelBooking.repository;

import com.aplusplus.HotelBooking.model.Room;
import com.google.storage.v2.ObjectChecksums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoomRepo extends JpaRepository<Room, Long> {

    @Query(value = "SELECT * FROM rooms r WHERE r.room_status = 'Available'", nativeQuery = true)
    Page<Room> findAllRooms(Pageable pageable);
    @Query(value = "SELECT * FROM rooms r WHERE r.room_status = 'Available' AND r.room_capacity >= :numOfGuest AND r.room_amount > (SELECT COUNT(*) FROM bookings b WHERE b.room_id = r.id AND :checkInDate = b.check_in_date OR :checkInDate < b.check_in_date AND :checkOutDate > b.check_in_date OR :checkInDate > b.check_in_date AND :checkInDate < b.check_out_date)", nativeQuery = true)
    //@Query(value = "SELECT * FROM rooms r WHERE r.room_capacity >= :numOfGuest AND r.room_amount > (SELECT COUNT(*) FROM bookings b WHERE b.room_id = r.id AND :checkInDate = b.check_in_date OR :checkInDate < b.check_in_date AND :checkOutDate > b.check_in_date OR :checkInDate > b.check_in_date AND :checkInDate < b.check_out_date) OR r.room_capacity >= :numOfGuest AND NOT EXISTS (SELECT * FROM bookings b1 WHERE b1.room_id = r.id AND :checkInDate = b1.check_in_date OR :checkInDate < b1.check_in_date AND :checkOutDate > b1.check_in_date OR :checkInDate > b1.check_in_date AND :checkInDate < b1.check_out_date)", nativeQuery = true)
    Page<Room> findByDateAndNumOfGuest(LocalDate checkInDate, LocalDate checkOutDate, int numOfGuest, Pageable pageable);

    List<Room> findByRoomType(String roomType);

    @Query(value = "SELECT AVG(review_rate) FROM reviews WHERE room_id = :roomId", nativeQuery = true)
    Double getAverageRating(Long roomId);

    @Query(value = "SELECT COUNT(review_rate) FROM reviews WHERE room_id = :roomId", nativeQuery = true)
    Long getNumberOfRating(Long roomId);

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE room_id = :roomId", nativeQuery = true)
    Long getNumberOfBooking(Long roomId);

    @Query(value = "SELECT MAX(percent_of_discount) FROM promotions p JOIN room_promotion rp ON p.id = rp.promotion_id WHERE room_id = :roomId AND end_date >= :now", nativeQuery = true)
    Integer getMaxDiscount(Long roomId, LocalDate now);

    @Query(value = "SELECT room_type, COUNT(room_type) as total_bookings " +
            "FROM bookings b JOIN payments p ON b.id = p.booking_id " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE YEAR(check_in_date) = :year AND MONTH(check_in_date) = :month AND payment_status = 'PAID'" +
            "GROUP BY room_type " +
            "ORDER BY total_bookings DESC " +
            "LIMIT 1", nativeQuery = true)
    Map<String, Object> getTrendingRoomAndNumberOfBookingsByYearAndMonth(Integer year, Integer month);
}
