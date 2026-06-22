package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.BookingDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.RoomDTO;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.BookingMapper;
import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.model.Room;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.repository.BookingRepo;
import com.aplusplus.HotelBooking.repository.PaymentRepo;
import com.aplusplus.HotelBooking.repository.RoomRepo;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.EmailService;
import com.aplusplus.HotelBooking.service.interf.IBookingService;
import com.aplusplus.HotelBooking.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
    private final BookingRepo bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepo userRepo;
    private final RoomRepo roomRepo;
    private final PaymentRepo paymentRepo;
    private final RoomService roomService;
    private final Utils utils;
    private final EmailService emailService;
    @Override
    public Response createBooking(Long roomId, String username, Booking bookingRequest) {
        Response response = new Response();
        try{
            // Check legal for check out date and check in date
            if(bookingRequest.getCheckInDate().isBefore(LocalDate.now()) || bookingRequest.getCheckOutDate().isBefore(LocalDate.now())){
                throw new IllegalArgumentException("Check in date and check out date must come after now");
            }
            if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
                throw new IllegalArgumentException("Check out date must come after check in date");
            }
            //Get room, user belong to booking
            String email = utils.getCurrentUsername();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
            User user = userRepo.findByEmail(email).orElseThrow(() -> new OurException("User not found"));

            // Check if there are remaining rooms or not
            if(roomService.remainingRoomAmount(room, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) == 0){
                throw new OurException("No available room for selected date range");
            }
            // Check if room's capacity meet customer need or not
            if(room.getRoomCapacity() < bookingRequest.getNumOfChildren() + bookingRequest.getNumOfAdults()){
                throw new OurException("This room capacity can't meet your need, please choose another room");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);

            // Add discount and final price
            Integer percentOfDiscount = roomRepo.getMaxDiscount(roomId, LocalDate.now());
            if(percentOfDiscount == null) percentOfDiscount = 0;
            Double finalPrice = (room.getRoomPrice() - room.getRoomPrice()*percentOfDiscount/100) * ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
            bookingRequest.setPercentOfDiscount(percentOfDiscount);
            bookingRequest.setFinalPrice(finalPrice);

            String bookingCode = utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingCode(bookingCode);
            bookingRequest.setTotalNumOfGuest(bookingRequest.getNumOfAdults() + bookingRequest.getNumOfChildren());
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("Booking room successfully");
            response.setBookingCode(bookingCode);

            //Send email to user
            emailService.sendBookingConfirmationEmail(bookingRequest);
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
    public Response getBookingById(String bookingId) {
        Response response = new Response();
        try{
            var booking = bookingRepository.findById(Long.parseLong(bookingId)).orElseThrow(() -> new OurException("Booking not found"));
            BookingDTO bookingDTO = bookingMapper.toBookingDTO(booking);
            if(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1) bookingDTO.setPaymentStatus("PAID");

            response.setBooking(bookingDTO);
            response.setStatusCode(200);
            response.setMessage("Find booking information successfully");
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
    //For admin to see all bookings of customers in a range of date
    public Response getBookingsByDateAndRoomType(String roomType, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Response response = new Response();
        try{
            Page<BookingDTO> bookingDTOPage = bookingRepository.findAll(pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList;

            // Set value for bookingDTOPage with each situation

            // Miss roomType
            if (roomType == null && startDate != null && endDate != null){
                // Check legal for start date and end date
                if(endDate.isBefore(startDate)) throw new IllegalArgumentException("End date must come after start date");
                bookingDTOPage = bookingRepository.getBookingByDate(startDate, endDate, pageable).map(bookingMapper::toBookingDTO);
            }
            // Miss start date or end date
            else if (roomType != null && startDate == null || roomType != null && endDate == null){
                bookingDTOPage = bookingRepository.getBookingsByRoomType(roomType, pageable).map(bookingMapper::toBookingDTO);
            }
            // Full of params
            else if (roomType != null){
                // Check legal for start date and end date
                if(endDate.isBefore(startDate)) throw new IllegalArgumentException("End date must come after start date");
                bookingDTOPage = bookingRepository.getBookingsByDateAndRoomType(roomType, startDate, endDate, pageable).map(bookingMapper::toBookingDTO);
            }

            bookingDTOList = bookingDTOPage.getContent();
            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );
            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Successful");
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBooking(Pageable pageable) {
        Response response = new Response();
        try{
            Page<BookingDTO> bookingDTOPage = bookingRepository.findAll(pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();

            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Find all booking information successfully");
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    // Get booking history of user
    public Response getBookingsByUsername(String username, Pageable pageable) {
        Response response = new Response();
        try{

            User user = userRepo.findByEmail(username).orElseThrow(() -> new OurException("User not found"));
            Page<BookingDTO> bookingDTOPage = bookingRepository.getBookingsByUser(user.getId(), pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();
            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Find booking information successfully");
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
    // For admin: Get all current bookings of room
    public Response getBookingsByRoomId(String roomId, Pageable pageable) {
        Response response = new Response();

        try{
            LocalDate now = LocalDate.now();
            Page<BookingDTO> bookingDTOPage = bookingRepository.getBookingsByRoom(Long.valueOf(roomId), now, pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();
            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Successful");
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
    public Response updateBooking(BookingDTO request) {
        return null;
    }

    @Override
    public Response cancelBooking(String bookingId) {
        // TODO code to cancel booking (delete booking)
        Response response = new Response();
        try{
            var booking = bookingRepository.findById(Long.parseLong(bookingId)).orElseThrow(() -> new OurException("Booking not found"));
            bookingRepository.delete(booking);

            response.setStatusCode(200);
            response.setMessage("Cancel booking successfully");
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
    public Response getRecentBookings(Long userId, LocalDate now, Pageable pageable) {
        Response response = new Response();
        try{
            Page<BookingDTO> bookingDTOPage = bookingRepository.getRecentBookings(userId, now, pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();
            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Successful");
        } catch(Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response bookingHistory(String username, Pageable pageable) {
        Response response = new Response();
        try{

            User user = userRepo.findByEmail(username).orElseThrow(() -> new OurException("User not found"));
            Page<BookingDTO> bookingDTOPage = bookingRepository.getBookingsHistory(user.getId(), pageable).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();
            bookingDTOList.forEach(bookingDTO ->
                    bookingDTO.setPaymentStatus(paymentRepo.isPaid(bookingDTO.getBookingCode()) == 1 ? "PAID" : "UNPAID")
            );

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Find booking information successfully");
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
    public Response getBookingsOutOfDue(Pageable pageable) {
        Response response = new Response();
        try {
            Page<BookingDTO> bookingDTOPage = bookingRepository.getBookingsOutOfDue(pageable, LocalDate.now()).map(bookingMapper::toBookingDTO);
            List<BookingDTO> bookingDTOList = bookingDTOPage.getContent();

            response.setBookingList(bookingDTOList);
            response.setCurrentPage(bookingDTOPage.getNumber());
            response.setTotalElements(bookingDTOPage.getTotalElements());
            response.setTotalPages(bookingDTOPage.getTotalPages());
            response.setStatusCode(200);
            response.setMessage("Find bookings out of due successfully!");
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteBookingOutOfDue(String bookingId) {
        Response response = new Response();
        try {
            Booking booking = bookingRepository.findById(Long.valueOf(bookingId)).orElseThrow(() -> new OurException("Booking not found"));
            bookingRepository.delete(booking);
            emailService.sendBookingCancellationEmail(booking);
            response.setStatusCode(200);
            response.setMessage("Delete booking successfully!");
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
