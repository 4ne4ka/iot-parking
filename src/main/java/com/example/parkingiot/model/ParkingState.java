package com.example.parkingiot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_state")
public class ParkingState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_spaces")
    private Integer totalSpaces = 100;   // всего мест

    @Column(name = "occupied_spaces")
    private Integer occupiedSpaces = 0;  // занято

    @Column(name = "entry_count")
    private Integer entryCount = 0;      // всего въехало (счетчик)

    @Column(name = "exit_count")
    private Integer exitCount = 0;       // всего выехало (счетчик)

    // Состояния систем
    @Column(name = "ventilation_on")
    private Boolean ventilationOn = false;

    @Column(name = "lighting_on")
    private Boolean lightingOn = false;

    @Column(name = "entry_barrier_open")
    private Boolean entryBarrierOpen = false;

    @Column(name = "exit_barrier_open")
    private Boolean exitBarrierOpen = false;

    @Column(name = "drain_pump_on")
    private Boolean drainPumpOn = false;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ParkingState() {
        this.updatedAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры (сгенерируйте их все через Alt+Insert или правый клик → Generate → Getter and Setter)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTotalSpaces() { return totalSpaces; }
    public void setTotalSpaces(Integer totalSpaces) { this.totalSpaces = totalSpaces; }

    public Integer getOccupiedSpaces() { return occupiedSpaces; }
    public void setOccupiedSpaces(Integer occupiedSpaces) { this.occupiedSpaces = occupiedSpaces; }

    public Integer getEntryCount() { return entryCount; }
    public void setEntryCount(Integer entryCount) { this.entryCount = entryCount; }

    public Integer getExitCount() { return exitCount; }
    public void setExitCount(Integer exitCount) { this.exitCount = exitCount; }

    public Boolean getVentilationOn() { return ventilationOn; }
    public void setVentilationOn(Boolean ventilationOn) { this.ventilationOn = ventilationOn; }

    public Boolean getLightingOn() { return lightingOn; }
    public void setLightingOn(Boolean lightingOn) { this.lightingOn = lightingOn; }

    public Boolean getEntryBarrierOpen() { return entryBarrierOpen; }
    public void setEntryBarrierOpen(Boolean entryBarrierOpen) { this.entryBarrierOpen = entryBarrierOpen; }

    public Boolean getExitBarrierOpen() { return exitBarrierOpen; }
    public void setExitBarrierOpen(Boolean exitBarrierOpen) { this.exitBarrierOpen = exitBarrierOpen; }

    public Boolean getDrainPumpOn() { return drainPumpOn; }
    public void setDrainPumpOn(Boolean drainPumpOn) { this.drainPumpOn = drainPumpOn; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Вычисляемое поле
    public Integer getFreeSpaces() {
        return totalSpaces - occupiedSpaces;
    }
}