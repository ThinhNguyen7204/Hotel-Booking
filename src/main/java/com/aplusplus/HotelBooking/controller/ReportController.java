package com.aplusplus.HotelBooking.controller;

import com.aplusplus.HotelBooking.dto.Response;
import com.aplusplus.HotelBooking.service.implement.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    @Autowired
    private ReportService reportService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Response> getReport(@RequestParam Integer year){
        Response response = reportService.getReport(year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
