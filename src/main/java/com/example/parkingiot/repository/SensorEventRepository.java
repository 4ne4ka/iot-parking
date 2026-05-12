package com.example.parkingiot.repository;

import com.example.parkingiot.model.SensorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorEventRepository extends JpaRepository<SensorEvent, Long> {

    // Найти все события определенного датчика за период
    List<SensorEvent> findBySensorTypeAndCreatedAtBetween(
            String sensorType,
            LocalDateTime start,
            LocalDateTime end
    );

    // Посчитать количество срабатываний датчика за период
    long countBySensorTypeAndValueAndCreatedAtBetween(
            String sensorType,
            Integer value,
            LocalDateTime start,
            LocalDateTime end
    );
}