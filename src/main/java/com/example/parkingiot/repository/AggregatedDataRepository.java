package com.example.parkingiot.repository;

import com.example.parkingiot.model.AggregatedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AggregatedDataRepository extends JpaRepository<AggregatedData, Long> {

    // Найти незакрытый период
    Optional<AggregatedData> findFirstByOrderByPeriodStartDesc();
}