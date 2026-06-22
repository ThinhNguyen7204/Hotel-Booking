package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.ReviewDTO;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    Response createReview(ReviewDTO review, Long roomId);
    Response getReviewById(String reviewId);
    Response getReviewByUserId(String userId, Pageable pageable);
    Response getReviewByRoomId(String roomId, Pageable pageable);
    Response updateReview(ReviewDTO review, String reviewId);
    Response deleteReview(String reviewId);
    Response likeReview(String reviewId, Boolean isLike);
}
