package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.FacilityDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.RoomDTO;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.FacilityMapper;
import com.aplusplus.HotelBooking.mapper.RoomMapper;
import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.model.Facility;
import com.aplusplus.HotelBooking.model.Room;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.FacilityRepo;
import com.aplusplus.HotelBooking.repository.RoomRepo;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.FirebaseStorageService;
import com.aplusplus.HotelBooking.service.interf.IRoomService;
import com.aplusplus.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private FacilityRepo facilityRepo;
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    @Autowired
    private Utils utils;
    @Autowired
    private UserRepo userRepo;
    @Override
    public Response getRoom(String roomId) {
        Response response = new Response();
        try{
            Room room = roomRepo.findById(Long.valueOf(roomId)).orElseThrow(() -> new OurException("Room not found"));
            RoomDTO roomDTO = roomMapper.roomToRoomDTO(room);
            // Get room statistic and new price with promotion
            roomDTO = setRoomStatistic(roomDTO);

            response.setRoom(roomDTO);
            response.setStatusCode(200);
            response.setMessage("Get room with id: " + roomId + " successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllRoom(Pageable pageable) {
        Response response = new Response();
        try{
            Page<RoomDTO> roomDTOPage;
            User user = userRepo.findByEmail(utils.getCurrentUsername()).orElseThrow(() -> new OurException("User not found"));
            if(Objects.equals(user.getRole(), "ADMIN"))
                roomDTOPage = roomRepo.findAll(pageable).map(roomMapper::roomToRoomDTO);
            else
                roomDTOPage = roomRepo.findAllRooms(pageable).map(roomMapper::roomToRoomDTO);
            List<RoomDTO> roomDTOList = roomDTOPage.getContent();
            // Set room statistic
            for(RoomDTO roomDTO : roomDTOList){
                roomDTO = setRoomStatistic(roomDTO);
            }
            response.setRoomList(roomDTOList);
            response.setCurrentPage(roomDTOPage.getNumber());
            response.setTotalPages(roomDTOPage.getTotalPages());
            response.setTotalElements(roomDTOPage.getTotalElements());
            response.setStatusCode(200);
            response.setMessage("Get all rooms successfully");
        } catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response addRoom(
            Room newRoom,
            MultipartFile roomPhoto,
            Facility facility) {
        Response response = new Response();
        try{
            if(roomPhoto != null){
                String fileUrl = firebaseStorageService.uploadFile(roomPhoto);
                newRoom.setRoomPhotoUrl(fileUrl);
            }
            facilityRepo.save(facility);
            newRoom.setFacility(facility);
            roomRepo.save(newRoom);
            response.setStatusCode(200);
            response.setMessage("Add new room successfully");
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateRoom(
            Long roomId,
            Room updatedRoom,
            MultipartFile roomPhoto,
            Facility facility) {
        Response response = new Response();
        try{
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));

            room.setRoomType(updatedRoom.getRoomType());
            room.setRoomSize(updatedRoom.getRoomSize());
            room.setRoomPrice(updatedRoom.getRoomPrice());
            room.setRoomDescription(updatedRoom.getRoomDescription());
            room.setRoomStatus(updatedRoom.getRoomStatus());
            room.setRoomCapacity(updatedRoom.getRoomCapacity());
            room.setRoomAmount(updatedRoom.getRoomAmount());

            if(roomPhoto != null){
                String fileUrl = firebaseStorageService.uploadFile(roomPhoto);
                room.setRoomPhotoUrl(fileUrl);
            }

            Facility existedFacility = facilityRepo.findByRoom(room);
            existedFacility.setGymInfo(facility.getGymInfo());
            existedFacility.setBathInfo(facility.getBathInfo());
            existedFacility.setDrinkInfo(facility.getDrinkInfo());
            existedFacility.setCoffeeInfo(facility.getCoffeeInfo());
            existedFacility.setBreakfastInfo(facility.getBreakfastInfo());
            existedFacility.setParkingInfo(facility.getParkingInfo());
            existedFacility.setWifiInfo(facility.getWifiInfo());
            existedFacility.setPoolInfo(facility.getPoolInfo());
            facilityRepo.save(existedFacility);
            room.setFacility(existedFacility);
            roomRepo.save(room);
            response.setStatusCode(200);
            response.setMessage("Update room successfully");
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
    public Response deleteRoom(String roomId) {
        Response response = new Response();
        try{
            Room room = roomRepo.findById(Long.valueOf(roomId)).orElseThrow(() -> new OurException("Room not found"));
            roomRepo.deleteById(Long.valueOf(roomId));
            response.setStatusCode(200);
            response.setMessage("Delete room successfully");
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
    public Response getAvailableRoomsByDateAndNumOfGuest(LocalDate checkInDate, LocalDate checkoutDate, int numOfGuest, Pageable pageable) {
        Response response = new Response();
        try{
            // Check legal for check out date and check in date
            if(checkInDate.isBefore(LocalDate.now()) || checkoutDate.isBefore(LocalDate.now())){
                throw new IllegalArgumentException("Check in date and check out date must come after now");
            }
            if(checkoutDate.isBefore(checkInDate)){
                throw new IllegalArgumentException("Check out date must come after check in date");
            }
            Page<Room> roomPage = roomRepo.findByDateAndNumOfGuest(checkInDate,checkoutDate,numOfGuest, pageable);
            List<Room> roomList = roomPage.getContent();
            List<RoomDTO> roomDTOList = roomMapper.roomListToRoomDTOList(roomList);
            // Count existed booking conflict to this date range and show remaining room amount
            for(int i = 0; i < roomList.size(); i++){
                List<Booking> roomBooking = roomList.get(i).getBookings();
                int count = 0; // Num of bookings that conflict with recent booking
                for (Booking booking : roomBooking) {
                    if (checkConflict(checkInDate, checkoutDate, booking.getCheckInDate(), booking.getCheckOutDate()))
                        count++;
                }
                roomDTOList.get(i).setRemain(roomDTOList.get(i).getRoomAmount() - count);
                roomDTOList.get(i).setRoomStatus("Còn " + roomDTOList.get(i).getRemain() + " phòng trống");
            }

            //Set room statistic
            for(RoomDTO roomDTO : roomDTOList){
                roomDTO = setRoomStatistic(roomDTO);
            }
            response.setRoomList(roomDTOList);
            response.setCurrentPage(roomPage.getNumber());
            response.setTotalPages(roomPage.getTotalPages());
            response.setTotalElements(roomPage.getTotalElements());
            response.setStatusCode(200);
            response.setMessage("Get all available rooms by date successfully");
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response checkAvailable(LocalDate checkInDate, LocalDate checkOutDate, int totalGuest, Long roomId) {
        Response response = new Response();
        try{
            // Check legal for check out date and check in date
            if(checkInDate.isBefore(LocalDate.now()) || checkOutDate.isBefore(LocalDate.now())){
                throw new IllegalArgumentException("Check in date and check out date must come after now");
            }
            if(checkOutDate.isBefore(checkInDate)){
                throw new IllegalArgumentException("Check out date must come after check in date");
            }

            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            RoomDTO roomDTO = roomMapper.roomToRoomDTO(room);
            if(room.getRoomCapacity() < totalGuest) throw new OurException("Sức chứa của phòng không đủ đáp ứng nhu cầu của bạn, vui lòng chọn loại phòng khác lớn hơn!");

            List<Booking> bookingList = room.getBookings();
            int count = 0; // Use for count num of conflicted bookings
            for(Booking booking : bookingList){
                if(checkConflict(checkInDate, checkOutDate, booking.getCheckInDate(), booking.getCheckOutDate()))
                    count++;
            }
            roomDTO.setRemain(room.getRoomAmount() - count);
            if(roomDTO.getRemain() == 0) throw  new OurException("Không còn phòng trống cho loại phòng này, vui lòng chọn phòng khác!");
            roomDTO.setRoomStatus("Còn " + roomDTO.getRemain() + " phòng trống");

            response.setRoom(roomDTO);
            response.setStatusCode(200);
            response.setMessage("Get available room successfully");
        } catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }  catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Check conflict in check in date and check out date
    public boolean checkConflict(LocalDate checkInDate, LocalDate checkOutDate, LocalDate existedCheckInDate, LocalDate existedCheckOutDate){
        return (
                checkInDate.equals(existedCheckInDate)
                || checkInDate.isBefore(existedCheckInDate) && checkOutDate.isAfter(existedCheckInDate)
                || checkInDate.isAfter(existedCheckInDate) && checkInDate.isBefore(existedCheckOutDate)
                );
    }

    public int remainingRoomAmount(Room room, LocalDate checkInDate, LocalDate checkOutDate){
        List<Booking> bookingList = room.getBookings();
        int count = 0;
        for(Booking booking : bookingList){
            if(checkConflict(checkInDate, checkOutDate, booking.getCheckInDate(), booking.getCheckOutDate()))
                count++;
        }
        return room.getRoomAmount() - count;
    }

    public RoomDTO setRoomStatistic(RoomDTO roomDTO){
        Double averageRating = roomRepo.getAverageRating(roomDTO.getId());
        if(averageRating == null) averageRating = 0.;
        Long numberOfRating = roomRepo.getNumberOfRating(roomDTO.getId());
        Long numberOfBooking = roomRepo.getNumberOfBooking(roomDTO.getId());
        Integer maxDiscount = roomRepo.getMaxDiscount(roomDTO.getId(), LocalDate.now());
        if(maxDiscount == null) maxDiscount = 0;
        Double newPrice = roomDTO.getRoomPrice() - roomDTO.getRoomPrice()*maxDiscount/100.0;

        roomDTO.setAverageRating(averageRating);
        roomDTO.setNumberOfRating(numberOfRating);
        roomDTO.setNumberOfBooking(numberOfBooking);
        roomDTO.setPercentOfDiscount(maxDiscount.longValue());
        roomDTO.setNewPrice(newPrice.longValue());

        return roomDTO;
    }
}
