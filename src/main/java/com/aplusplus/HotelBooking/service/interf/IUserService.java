package com.aplusplus.HotelBooking.service.interf;

import com.aplusplus.HotelBooking.dto.LoginRequest;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.UserDTO;
import com.aplusplus.HotelBooking.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    Response register (User user);
    Response login(LoginRequest loginRequest);
    Response getAllCustomers(Pageable pageable);
    Response getUserBookingHistory(String userId);
    Response deleteUser(String username);
    Response getUserById(String userId);
    Response getMyInfo(String username);

    Response updateInfo(String username, UserDTO userDTO);
    Response uploadImage(String username, MultipartFile image);
}
