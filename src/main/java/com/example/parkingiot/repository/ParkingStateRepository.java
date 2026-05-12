package com.example.parkingiot.repository;

import com.example.parkingiot.model.ParkingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingStateRepository extends JpaRepository<ParkingState, Long> {

    // Получить самое свежее состояние (первое попавшееся)
    Optional<ParkingState> findFirstByOrderByIdDesc();
}