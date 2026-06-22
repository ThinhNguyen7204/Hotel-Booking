package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.PromotionDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.mapper.PromotionMapper;
import com.aplusplus.HotelBooking.model.Promotion;
import com.aplusplus.HotelBooking.model.Room;
import com.aplusplus.HotelBooking.repository.PromotionRepo;
import com.aplusplus.HotelBooking.repository.RoomRepo;
import com.aplusplus.HotelBooking.service.FirebaseStorageService;
import com.aplusplus.HotelBooking.service.interf.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService {
    private final PromotionRepo promotionRepository;
    private final PromotionMapper promotionMapper;
    private final RoomRepo roomRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Override
    public Response createPromotionForRoomType(PromotionDTO promotionDTO, String[] listRoomType, MultipartFile imageFile) {
        Response response = new Response();
        try {
            Promotion promotion = promotionMapper.toPromotion(promotionDTO);
            List<Room> roomListPromotion = new ArrayList<>();

            List<List<Room>> listRoomByPromotions = new ArrayList<>();

            for (int i = 0; i < listRoomType.length; i++) {
                List<Room> rooms = roomRepository.findByRoomType(listRoomType[i]);
                if (rooms.size() == 0) {
                    throw new Exception("Room type not found!!");
                }
                listRoomByPromotions.add(rooms);
            }

            List<Room> roomListPromotions = new ArrayList<>();
            for (List<Room> rooms : listRoomByPromotions) {
                for (Room room : rooms) {
                    room.getPromotions().add(promotion);
                    roomListPromotions.add(room);
                }
            }
            promotion.setRooms(roomListPromotions);


//            for (int i = 0; i < listRoomType.length; i++) {
//                List<Room> rooms = roomRepository.findByRoomType(listRoomType[i]);
//                for (Room room : rooms) {
//                    room.getPromotions().add(promotion);
//                }
//                roomListPromotion.addAll(rooms);
//            }
//            promotion.setRooms(roomListPromotion);

            // save image to firebase
            if (imageFile.getSize() != 0) {
                String imageUrl = firebaseStorageService.uploadFile(imageFile);
                promotion.setPromotionPhotoUrl(imageUrl);
            }

            promotionRepository.save(promotion);


            response.setStatusCode(200);
            response.setMessage("Create promotion successfully");
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response getPromotionById(String promotionId) {
        Response response = new Response();
        try {
            Promotion promotion = promotionRepository.findById(Long.parseLong(promotionId)).orElseThrow(() -> new Exception("Promotion not found"));
            PromotionDTO promotionDTO = promotionMapper.toPromotionDTO(promotion);

            List<Room> rooms = promotion.getRooms();
            List<String> listRoomType = new ArrayList<>();

            for (Room room : rooms) {
                if (!listRoomType.contains(room.getRoomType())) {
                    listRoomType.add(room.getRoomType());
                }
            }
            listRoomType.sort(String::compareTo);
            promotionDTO.setListRoomTypes(listRoomType.toArray(new String[0]));

            response.setStatusCode(200);
            response.setPromotion(promotionDTO);
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllPromotion(Pageable pageable) {
        Response response = new Response();
        try {
            List<Promotion> promotions = promotionRepository.findAll(pageable).getContent();
            List<PromotionDTO> promotionDTOS = new ArrayList<>();

            for (Promotion promotion : promotions) {
                PromotionDTO promotionDTO = promotionMapper.toPromotionDTO(promotion);
                List<Room> rooms = promotion.getRooms();
                List<String> listRoomType = new ArrayList<>();

                for (Room room : rooms) {
                    if (!listRoomType.contains(room.getRoomType())) {
                        listRoomType.add(room.getRoomType());
                    }
                }
                listRoomType.sort(String::compareTo);
                promotionDTO.setListRoomTypes(listRoomType.toArray(new String[0]));
                promotionDTOS.add(promotionDTO);
            }
            response.setCurrentPage(pageable.getPageNumber());
            response.setTotalPages(promotionRepository.findAll(pageable).getTotalPages());
            response.setTotalElements(promotionRepository.findAll(pageable).getTotalElements());
            response.setStatusCode(200);
            response.setPromotionList(promotionDTOS);
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response updatePromotion(PromotionDTO newPromotion, String promotionId, MultipartFile imageFile) {
        Response response = new Response();
        try {
            Promotion promotionUpdate = promotionRepository.findById(Long.parseLong(promotionId)).orElseThrow(() -> new Exception("Promotion not found"));
            promotionUpdate.setPromotionTitle(newPromotion.getPromotionTitle());
            promotionUpdate.setDescription(newPromotion.getDescription());
            promotionUpdate.setStartDate(newPromotion.getStartDate());
            promotionUpdate.setEndDate(newPromotion.getEndDate());
            promotionUpdate.setPercentOfDiscount(newPromotion.getPercentOfDiscount());
            if (newPromotion.getListRoomTypes() != null) {

                // for removing room type from promotion
                List<String> listOfNewRoomType = List.of(newPromotion.getListRoomTypes());
//                boolean[] isRoomTypeExist = new boolean[listOfNewRoomType.size()];

                // false when room type is not exist in new promotion
                for (Room room : promotionUpdate.getRooms()) {
                    if (!listOfNewRoomType.contains(room.getRoomType())) {
                        room.getPromotions().remove(promotionUpdate);
                    }
                }

                ArrayList<List<Room>> listRoomByPromotions = new ArrayList<>();

                for (int i = 0; i < listOfNewRoomType.toArray().length; i++) {
                    List<Room> rooms = roomRepository.findByRoomType(listOfNewRoomType.get(i));

                    if (rooms.size() == 0) {
                        throw new Exception("Room type not found!!");
                    }
                    listRoomByPromotions.add(rooms);
                }

                List<Room> roomListPromotions = new ArrayList<>();
                for (List<Room> rooms : listRoomByPromotions) {
                    for (Room room : rooms) {
                        if (!promotionUpdate.getRooms().contains(room)) {
                            room.getPromotions().add(promotionUpdate);
                            roomListPromotions.add(room);
                        }
                    }
                }

                promotionUpdate.setRooms(roomListPromotions);

                // save image to firebase
                if (imageFile.getSize() != 0) {
                    String imageUrl = firebaseStorageService.uploadFile(imageFile);
                    promotionUpdate.setPromotionPhotoUrl(imageUrl);
                }

                promotionRepository.save(promotionUpdate);
            }

            response.setStatusCode(200);
            response.setMessage("Update newPromotion successfully");
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deletePromotion(String promotionId) {
        Response response = new Response();
        try {
            Promotion promotion = promotionRepository.findById(Long.parseLong(promotionId)).orElseThrow(() -> new Exception("Promotion not found"));
            List<Room> rooms = promotion.getRooms();
            for (Room room : rooms) {
                room.getPromotions().remove(promotion);
            }
            promotionRepository.delete(promotion);

            response.setStatusCode(200);
            response.setMessage("Delete promotion successfully");
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response applyPromotionToRoom(String promotionId, String roomId) {
        return null;
    }

    @Override
    public Response getPromotionsByRoomId(String roomId, Pageable pageable) {
        Response response = new Response();
        try {
            Room room = roomRepository.findById(Long.parseLong(roomId)).orElseThrow(() -> new Exception("Room not found"));
            List<Promotion> promotions = room.getPromotions();

            // Manually paginate the list
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            List<Promotion> paginatedPromotions;

            List<PromotionDTO> promotionDTOS = new ArrayList<>();

            if (startItem >= promotions.size()) {
                paginatedPromotions = List.of(); // Empty list if startItem exceeds size
            } else {
                int endItem = Math.min(startItem + pageSize, promotions.size());
                paginatedPromotions = promotions.subList(startItem, endItem);
            }

            for (Promotion promotion : paginatedPromotions) {
                PromotionDTO promotionDTO = promotionMapper.toPromotionDTO(promotion);

                List<Room> rooms = promotion.getRooms();
                List<String> listRoomType = new ArrayList<>();

                for (Room roomPromotion : rooms) {
                    if (!listRoomType.contains(roomPromotion.getRoomType())) {
                        listRoomType.add(roomPromotion.getRoomType());
                    }
                }
                listRoomType.sort(String::compareTo);
                promotionDTO.setListRoomTypes(listRoomType.toArray(new String[0]));
                promotionDTOS.add(promotionDTO);
            }

            response.setStatusCode(200);
            response.setPromotionList(promotionDTOS);
            response.setCurrentPage(currentPage);
            response.setTotalPages(promotions.size() / pageSize);
            response.setTotalElements((long) promotions.size());
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getLatestPromotion(Pageable pageable) {
        Response response = new Response();
        try {
            List<Promotion> promotions = promotionRepository.findTop3ByCurrentDateBetweenStartDateAndEndDateOrderByStartDateDesc(pageable).getContent();
            List<PromotionDTO> promotionDTOS = new ArrayList<>();

            for (Promotion promotion : promotions) {
                PromotionDTO promotionDTO = promotionMapper.toPromotionDTO(promotion);
                List<Room> rooms = promotion.getRooms();
                List<String> listRoomType = new ArrayList<>();

                for (Room room : rooms) {
                    if (!listRoomType.contains(room.getRoomType())) {
                        listRoomType.add(room.getRoomType());
                    }
                }
                listRoomType.sort(String::compareTo);
                promotionDTO.setListRoomTypes(listRoomType.toArray(new String[0]));

                promotionDTOS.add(promotionDTO);
            }

            response.setCurrentPage(pageable.getPageNumber());
            response.setTotalPages(promotionRepository.findTop3ByCurrentDateBetweenStartDateAndEndDateOrderByStartDateDesc(pageable).getTotalPages());
            response.setTotalElements(promotionRepository.findTop3ByCurrentDateBetweenStartDateAndEndDateOrderByStartDateDesc(pageable).getTotalElements());
            response.setStatusCode(200);
            response.setPromotionList(promotionDTOS);
        }
        catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}