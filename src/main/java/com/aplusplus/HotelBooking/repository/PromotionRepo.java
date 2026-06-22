package com.aplusplus.HotelBooking.repository;

import com.aplusplus.HotelBooking.model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findById(Long id);

    Page<Promotion> findAll(Pageable pageable);

    @Query("SELECT p FROM Promotion p " +
            "ORDER BY p.startDate ASC")
    Page<Promotion> findTop3ByCurrentDateBetweenStartDateAndEndDateOrderByStartDateDesc(Pageable pageable);

}
