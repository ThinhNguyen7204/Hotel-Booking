package com.aplusplus.HotelBooking.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean drinkInfo;
    private Boolean gymInfo;
    private Boolean breakfastInfo;
    private Boolean poolInfo;
    private Boolean parkingInfo;
    private Boolean bathInfo;
    private Boolean coffeeInfo;
    private Boolean wifiInfo;

    @OneToOne(mappedBy = "facility")
    private Room room;
}
