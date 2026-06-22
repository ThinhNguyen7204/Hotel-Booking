package com.aplusplus.HotelBooking.controller;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import com.aplusplus.HotelBooking.dto.CreatePaymentLinkRequestBody;
import com.aplusplus.HotelBooking.model.Payment;
import com.aplusplus.HotelBooking.repository.BookingRepo;
import com.aplusplus.HotelBooking.repository.PaymentRepo;
import com.aplusplus.HotelBooking.repository.UserRepo;
import com.aplusplus.HotelBooking.service.implement.PaymentService;
import com.aplusplus.HotelBooking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PayOS payOS;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Utils utils;
    @Autowired
    private PaymentService paymentService;

    public PaymentController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @PostMapping(path = "/create")
    public ObjectNode createPaymentLink(@RequestBody CreatePaymentLinkRequestBody RequestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            final String productName = RequestBody.getProductName();
            final String description = RequestBody.getDescription();
            final String returnUrl = RequestBody.getReturnUrl();
            final String cancelUrl = RequestBody.getCancelUrl();
            final int price = RequestBody.getPrice();
            // Gen order code
            String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            ItemData item = ItemData.builder().name(productName).price(price).quantity(1).build();

            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).description(description).amount(price)
                    .item(item).returnUrl(returnUrl).cancelUrl(cancelUrl).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            Payment payment = new Payment();
            payment.setPaymentCode(orderCode);
            payment.setBooking(bookingRepo.findByBookingCode(description).orElseThrow(() -> new Exception("Booking not found")));
            payment.setUser(userRepo.findByEmail(utils.getCurrentUsername()).orElseThrow(() -> new Exception("User not found")));
            paymentRepo.save(payment);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "fail");
            response.set("data", null);
            return response;

        }
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(@PathVariable("orderId") long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }

    }

    @PutMapping(path = "/{orderId}")
    public ObjectNode cancelOrder(@PathVariable("orderId") int orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirmPayment(@RequestParam long orderCode){
        ObjectNode objectNodeResponse = getOrderById(orderCode);
        String targetUrl = "http://localhost:5173/recent-booking";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(targetUrl));
        paymentService.confirmPayment(orderCode, objectNodeResponse.get("data").get("status").asText());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}