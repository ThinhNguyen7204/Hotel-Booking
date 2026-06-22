package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FirebaseController {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) {
        try {
            // Upload file và lấy URL
            String fileUrl = firebaseStorageService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi upload file: " + e.getMessage());
        }
    }
}