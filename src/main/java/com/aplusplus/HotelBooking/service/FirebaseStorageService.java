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
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        // Trả về URL của file đã upload
        return blob.getMediaLink();
    }
}