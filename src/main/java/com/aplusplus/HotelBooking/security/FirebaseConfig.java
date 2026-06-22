package com.aplusplus.HotelBooking.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @PostConstruct
    public void init() throws IOException {
        String credentialsJson = System.getenv("FIREBASE_CREDENTIALS");
        InputStream serviceAccount;

        if (credentialsJson != null && !credentialsJson.trim().isEmpty()) {
            serviceAccount = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        } else {
            ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
            serviceAccount = resource.getInputStream();
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(storageBucket)
                .build();

        FirebaseApp.initializeApp(options);
    }
}