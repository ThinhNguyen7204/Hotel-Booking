package com.aplusplus.HotelBooking.service.implement;


import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.ReviewDTO;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.ReviewMapper;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.ReviewRepo;
import com.aplusplus.HotelBooking.repository.RoomRepo;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.interf.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepo reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepo userRepository;
    private final RoomRepo roomRepository;
    @Override
    public Response createReview(ReviewDTO reviewRequest, Long roomId) {
        Response response = new Response();
        try{
            var review = reviewMapper.toReview(reviewRequest);
            review.setCreatedTime(LocalDateTime.now());
            review.setLikeCounter(0);
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                // Assuming you have a method to get User entity by username
                Optional<User> optionalUser = userRepository.findByEmail(username);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    review.setUser(user);
                } else {
                    throw new OurException("User not found");
                }
            }
            review.setRoom(roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found")));
            reviewRepository.save(review);
            response.setStatusCode(200);
            response.setMessage("Create review successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewById(String reviewId) {
        Response response = new Response();
        try{
            var review = reviewRepository.findById(Long.parseLong(reviewId)).orElseThrow(() -> new OurException("Review not found"));

            response.setReview(reviewMapper.toReviewDTO(review));
            response.setStatusCode(200);
            response.setMessage("Create review successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewByUserId(String userId, Pageable pageable) {
        Response response = new Response();
        try{
            Page<ReviewDTO> reviewDTOPage = reviewRepository.findAllByUserId(Long.parseLong(userId), pageable).map(reviewMapper::toReviewDTO);
            List<ReviewDTO> reviewDTOList = reviewDTOPage.getContent();

            response.setReviewList(reviewDTOList);
            response.setCurrentPage(reviewDTOPage.getNumber());
            response.setTotalElements(reviewDTOPage.getTotalElements());
            response.setTotalPages(reviewDTOPage.getTotalPages());

            response.setStatusCode(200);
            response.setMessage("Get reviews successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getReviewByRoomId(String roomId, Pageable pageable) {
        Response response = new Response();
        try{
            Page<ReviewDTO> reviewDTOPage = reviewRepository.findAllByRoomId(Long.parseLong(roomId), pageable).map(reviewMapper::toReviewDTO);
            List<ReviewDTO> reviewDTOList = reviewDTOPage.getContent();

            response.setReviewList(reviewDTOList);
            response.setCurrentPage(reviewDTOPage.getNumber());
            response.setTotalElements(reviewDTOPage.getTotalElements());
            response.setTotalPages(reviewDTOPage.getTotalPages());

            response.setStatusCode(200);
            response.setMessage("Get reviews successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateReview(ReviewDTO reviewRequest, String reviewId) {
        Response response = new Response();
        try{
            var review = reviewRepository.findById(Long.parseLong(reviewId)).orElseThrow(() -> new OurException("Review not found"));
            review.setReviewRate(reviewRequest.getReviewRate());
            review.setComment(reviewRequest.getComment());
            review.setCreatedTime(LocalDateTime.now());
            reviewRepository.save(review);

            response.setStatusCode(200);
            response.setMessage("Update review successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteReview(String reviewId) {
        Response response = new Response();
        try{
            reviewRepository.deleteById(Long.parseLong(reviewId));
            response.setStatusCode(200);
            response.setMessage("Delete review successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response likeReview(String reviewId, Boolean isLike) {
        Response response = new Response();
        try{
            var review = reviewRepository.findById(Long.parseLong(reviewId)).orElseThrow(() -> new OurException("Review not found"));
            if(isLike)
                review.setLikeCounter(review.getLikeCounter() + 1);
            else
                review.setLikeCounter(review.getLikeCounter() - 1);
            reviewRepository.save(review);

            response.setReview(reviewMapper.toReviewDTO(review));
            response.setStatusCode(200);
            response.setMessage("Like review successfully");
        } catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
