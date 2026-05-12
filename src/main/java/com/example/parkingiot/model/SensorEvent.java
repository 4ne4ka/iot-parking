package com.example.parkingiot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_events")
public class SensorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_type")
    private String sensorType;   // EntryLoop, ExitLoop, GasSensor, SmokeSensor, TempSensor, LightSensor, LeakSensor, HybridReader

    private Integer value;        // 0 или 1

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Пустой конструктор (нужен JPA)
    public SensorEvent() {}

    // Конструктор для удобства
    public SensorEvent(String sensorType, Integer value) {
        this.sensorType = sensorType;
        this.value = value;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}