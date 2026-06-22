package com.aplusplus.HotelBooking.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Room type is required")
    private String roomType;
    @NotBlank(message = "Room size is required")
    private String roomSize;
    @NotNull(message = "Room price is required")
    private Double roomPrice;
    private String roomDescription;
    private String roomStatus;
//    private String bookingCode;
    private String roomPhotoUrl;
    @NotNull(message = "Room capacity is required")
    private int roomCapacity;
    @NotNull(message = "Room amount is required")
    private int roomAmount; // số lượng phòng có sẵn

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Booking> bookings = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "room_promotion",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions = new ArrayList<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> review;

    @OneToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private Facility facility;

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomType='" + roomType + '\'' +
                ", roomSize='" + roomSize + '\'' +
                ", roomPrice=" + roomPrice +
                ", roomDescription='" + roomDescription + '\'' +
                ", roomStatus='" + roomStatus + '\'' +
                ", roomPhotoUrl='" + roomPhotoUrl + '\'' +
                ", roomCapacity=" + roomCapacity +
                ", roomAmount=" + roomAmount +
                '}';
    }
}
