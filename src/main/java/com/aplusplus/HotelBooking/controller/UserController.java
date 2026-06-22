package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.dto.UserDTO;
import com.aplusplus.HotelBooking.model.User;
import com.aplusplus.HotelBooking.service.interf.IUserService;
import com.aplusplus.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private Utils utils;

    @GetMapping("/get_by_id/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") String userId){
        Response response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get_info")
    public ResponseEntity<Response> getUserInfo(){
        Response response = userService.getMyInfo(utils.getCurrentUsername());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update-info")
    public ResponseEntity<Response> updateUserInfo(@RequestBody UserDTO userDTO){
        Response response = userService.updateInfo(utils.getCurrentUsername(), userDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<Response> uploadAvatar(@RequestParam(value = "image") MultipartFile image){
        Response response = userService.uploadImage(utils.getCurrentUsername(), image);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get-all-customers")
    public ResponseEntity<Response> getAllCustomers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Response response = userService.getAllCustomers(pageable);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
