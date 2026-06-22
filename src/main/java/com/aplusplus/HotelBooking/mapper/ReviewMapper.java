package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.ReviewDTO;
import com.aplusplus.HotelBooking.model.Review;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    public abstract ReviewDTO toReviewDTO(Review review);
    public abstract Review toReview(ReviewDTO reviewDTO);
}
