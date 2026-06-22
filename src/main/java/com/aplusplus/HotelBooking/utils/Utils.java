package com.aplusplus.HotelBooking.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class Utils {
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWSYZ0123456789";

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateRandomConfirmationCode(int length){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++){
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            char randomChar = ALPHANUMERIC_STRING.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    public String getCurrentUsername() {
        // Lấy thông tin xác thực (authentication) từ SecurityContextHolder
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra nếu thông tin xác thực là một đối tượng UserDetails
        if (principal instanceof UserDetails) {
            // Trả về username từ đối tượng UserDetails
            return ((UserDetails) principal).getUsername();
        } else {
            // Nếu thông tin xác thực không phải là UserDetails, trả về chuỗi principal
            return principal.toString();
        }
    }
}
