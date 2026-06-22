package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.PromotionDTO;
import com.aplusplus.HotelBooking.dto.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IPromotionService {
    Response createPromotionForRoomType(PromotionDTO promotion, String[] listRoomType, MultipartFile imageFile); // thêm list các roomId
    Response getPromotionById(String promotionId); // Admin xem chi tiết promotion
    Response getAllPromotion(Pageable pageable); // Admin
    Response updatePromotion(PromotionDTO promotion, String promotionId, MultipartFile imageFile); // Admin
    Response deletePromotion(String promotionId);
    Response applyPromotionToRoom(String promotionId, String roomId); // Liên quan Booking
    Response getPromotionsByRoomId(String roomId, Pageable pageable); // List các promotion áp dụng cho 1 room
    Response getLatestPromotion(Pageable pageable); // User xem promotion mới nhất
}
