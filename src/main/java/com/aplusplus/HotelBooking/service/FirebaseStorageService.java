package com.aplusplus.HotelBooking.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file) throws IOException {
        // Tạo một tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Lấy bucket từ Firebase Storage
        Bucket bucket = StorageClient.getInstance().bucket();

        // Upload file lên Firebase Storage
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        
        // Trả về URL dạng Firebase Storage có thể truy cập công khai
        String encodedFileName = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8);
        return "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + encodedFileName + "?alt=media";
    }
}