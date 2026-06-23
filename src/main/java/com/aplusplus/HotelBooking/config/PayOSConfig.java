package com.aplusplus.HotelBooking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {
    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        System.out.println("=== PayOS Credentials Initialization Debug ===");
        System.out.println("Client ID: " + (clientId != null ? (clientId.substring(0, Math.min(5, clientId.length())) + "... (len: " + clientId.length() + ")") : "null"));
        System.out.println("API Key: " + (apiKey != null ? (apiKey.substring(0, Math.min(5, apiKey.length())) + "... (len: " + apiKey.length() + ")") : "null"));
        System.out.println("Checksum Key: " + (checksumKey != null ? (checksumKey.substring(0, Math.min(5, checksumKey.length())) + "... (len: " + checksumKey.length() + ")") : "null"));
        System.out.println("==============================================");
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
