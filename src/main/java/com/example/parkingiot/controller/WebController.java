package com.example.parkingiot.controller;

import com.example.parkingiot.service.ParkingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final ParkingService parkingService;

    public WebController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("status", parkingService.getFullStatus());
        return "dashboard";
    }
}