package com.example.parkingiot.controller;

import com.example.parkingiot.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Разрешаем запросы с любых источников (для разработки)
public class ApiController {

    private final ParkingService parkingService;

    public ApiController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    /**
     * Прием данных от Node-RED
     * POST /api/event
     * Body: {"EntryLoop": 1}
     */
    @PostMapping("/event")
    public ResponseEntity<String> receiveEvent(@RequestBody Map<String, Integer> data) {
        parkingService.processSensorData(data);
        return ResponseEntity.ok("{\"status\":\"ok\"}");  // ← Теперь это JSON
    }

    /**
     * Получить полное состояние парковки
     * GET /api/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(parkingService.getFullStatus());
    }

    /**
     * Ручное управление устройствами
     * POST /api/command
     * Body: {"device": "Ventilation", "action": "on"}
     */
    @PostMapping("/command")
    public ResponseEntity<String> sendCommand(@RequestBody Map<String, String> command) {
        // Заглушка для будущего ручного управления
        String device = command.get("device");
        String action = command.get("action");
        System.out.println("MANUAL COMMAND: " + device + " -> " + action);
        return ResponseEntity.ok("Command received: " + device + " " + action);
    }
}