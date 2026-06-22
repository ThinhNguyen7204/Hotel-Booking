package com.aplusplus.HotelBooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Percent of discount is required")
    private Integer percentOfDiscount;
    @NotBlank(message = "Promotion title is required")
    private String promotionTitle;
    private String description;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String promotionPhotoUrl;

    @ManyToMany(mappedBy = "promotions", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Room> rooms = new ArrayList<>();

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", percentOfDiscount=" + percentOfDiscount +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
