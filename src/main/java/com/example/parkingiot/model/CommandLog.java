package com.example.parkingiot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "command_log")
public class CommandLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;      // EntryBarrier, ExitBarrier, Ventilation, Lighting, DrainPump

    private String action;           // open, close, on, off

    private String triggeredBy;      // automatic, manual

    private String reason;           // почему сработало (например: "Low light level")

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CommandLog() {
        this.createdAt = LocalDateTime.now();
    }

    public CommandLog(String deviceName, String action, String triggeredBy, String reason) {
        this.deviceName = deviceName;
        this.action = action;
        this.triggeredBy = triggeredBy;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры (сгенерируйте все)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}