package com.aplusplus.HotelBooking.mapper;

import com.aplusplus.HotelBooking.dto.UserDTO;
import com.aplusplus.HotelBooking.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public abstract UserDTO userToUserDTO(User user);
    public abstract List<UserDTO> userListToUserDTOList(List<User> userList);
}
