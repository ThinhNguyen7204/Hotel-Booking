package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.ReviewDTO;
import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.service.interf.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping("/create-review/{roomId}")
    public ResponseEntity<Response> createReview(@RequestBody ReviewDTO review, @PathVariable String roomId){
        Response response = reviewService.createReview(review, Long.valueOf(roomId));
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-review-by-id/{id}")
    public ResponseEntity<Response> getReviewById(@PathVariable("id") String id){
        Response response = reviewService.getReviewById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-review-by-user-id/{userId}")
    public ResponseEntity<Response> getReviewByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdTime") String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Response response = reviewService.getReviewByUserId(userId, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-review-by-room-id/{roomId}")
    public ResponseEntity<Response> getReviewByRoomId(@PathVariable String roomId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdTime") String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Response response = reviewService.getReviewByRoomId(roomId, pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update-review/{id}")
    public ResponseEntity<Response> updateReview(@RequestBody ReviewDTO review, @PathVariable("id") String id){
        Response response = reviewService.updateReview(review, id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-review/{id}")
    public ResponseEntity<Response> deleteReview(@PathVariable("id") String id){
        Response response = reviewService.deleteReview(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/like-review/{id}")
    public ResponseEntity<Response> likeReview(@PathVariable("id") String id, @RequestParam Boolean isLike){
        Response response = reviewService.likeReview(id, isLike);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
