package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.LoginRequest;
import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.service.implement.UserService;
import com.aplusplus.HotelBooking.service.interf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    private ResponseEntity<Response> register(@RequestBody User user){
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest){
        Response response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
