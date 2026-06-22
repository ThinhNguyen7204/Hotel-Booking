package com.aplusplus.HotelBooking.repository;

import com.aplusplus.HotelBooking.model.Facility;
import com.aplusplus.HotelBooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacilityRepo extends JpaRepository<Facility, Long> {

    @Query("SELECT f FROM Facility f WHERE f.room=:room")
    Facility findByRoom(Room room);
}
