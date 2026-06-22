package com.aplusplus.HotelBooking.service.implement;

import com.aplusplus.HotelBooking.dto.ReportDTO;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.RoomDTO;
import com.aplusplus.HotelBooking.exception.OurException;
import com.aplusplus.HotelBooking.mapper.RoomMapper;
import com.aplusplus.HotelBooking.model.Room;
import com.aplusplus.HotelBooking.repository.BookingRepo;
import com.aplusplus.HotelBooking.repository.PaymentRepo;
import com.aplusplus.HotelBooking.repository.RoomRepo;
import com.aplusplus.HotelBooking.service.interf.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportService implements IReportService {
    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private RoomMapper roomMapper;
    public List<Long> getRevenuePerMonthByYear(Integer year) throws Exception{
        List<Long> revenuePerMonths = new ArrayList<>();
        for(int i = 1; i <=12 ; i++){
            Double monthRevenue = paymentRepo.getRevenueByYearAndMonth(year, i);
            if(monthRevenue != null)
                revenuePerMonths.add(monthRevenue.longValue());
            else{
                revenuePerMonths.add(0L);
            }
        }
        return revenuePerMonths;
    }

    public List<Long> getTotalBookingsPerMonthByYear(Integer year) throws Exception{
        List<Long> totalBookingsPerMonth = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            totalBookingsPerMonth.add(bookingRepo.getTotalBookingsByYearAndMonth(year, i));
        }
        return totalBookingsPerMonth;
    }

    public List<Long> getTrendingRoomBookingsPerMonthByYear(Integer year) throws Exception{
        List<Long> trendingRoomBookingsPerMonth = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            Map<String, Object> result = roomRepo.getTrendingRoomAndNumberOfBookingsByYearAndMonth(year, i);
            if(result.get("total_bookings") != null)
                trendingRoomBookingsPerMonth.add((Long) result.get("total_bookings"));
            else
                trendingRoomBookingsPerMonth.add(0L);
        }
        return trendingRoomBookingsPerMonth;
    }

    public List<String> getTrendingRoomPerMonthByYear(Integer year) throws Exception{
        List<String> trendingRoomPerMonth = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            Map<String, Object> result = roomRepo.getTrendingRoomAndNumberOfBookingsByYearAndMonth(year, i);
            String roomType= (String) result.get("room_type");
            if(roomType == null || roomType.isBlank()) trendingRoomPerMonth.add(null);
            else trendingRoomPerMonth.add(roomType);
        }
        return trendingRoomPerMonth;
    }

    @Override
    public Response getReport(Integer year) {
        Response response = new Response();
        try{
            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setRevenuePerMonth(getRevenuePerMonthByYear(year));
            reportDTO.setTotalBookingsPerMonth(getTotalBookingsPerMonthByYear(year));
            reportDTO.setTrendingRoomBookingsPerMonth(getTrendingRoomBookingsPerMonthByYear(year));
            reportDTO.setTrendingRooms(getTrendingRoomPerMonthByYear(year));

            response.setReport(reportDTO);
            response.setStatusCode(200);
            response.setMessage("Get report successfully");
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
