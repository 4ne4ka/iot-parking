package com.example.parkingiot.repository;

import com.example.parkingiot.model.CommandLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandLogRepository extends JpaRepository<CommandLog, Long> {

    // Последние N команд (для отображения на дашборде)
    List<CommandLog> findTop20ByOrderByCreatedAtDesc();

    // Команды за период
    List<CommandLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start,
            LocalDateTime end
    );
}