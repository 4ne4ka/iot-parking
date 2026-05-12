package com.example.parkingiot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_data")
public class AggregatedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_type")
    private String periodType;     // "15sec" — соответствует отправке из Node-RED

    @Column(name = "entry_count")
    private Integer entryCount = 0;

    @Column(name = "exit_count")
    private Integer exitCount = 0;

    @Column(name = "gas_alerts")
    private Integer gasAlerts = 0;

    @Column(name = "smoke_alerts")
    private Integer smokeAlerts = 0;

    @Column(name = "temp_alerts")
    private Integer tempAlerts = 0;

    @Column(name = "light_triggers")
    private Integer lightTriggers = 0;

    @Column(name = "leak_alerts")
    private Integer leakAlerts = 0;

    @Column(name = "cam_positive")
    private Integer camPositive = 0;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    public AggregatedData() {
        this.periodStart = LocalDateTime.now();
        this.periodEnd = LocalDateTime.now().plusSeconds(15);
    }

    // Геттеры и сеттеры (сгенерируйте все)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }

    public Integer getEntryCount() { return entryCount; }
    public void setEntryCount(Integer entryCount) { this.entryCount = entryCount; }

    public Integer getExitCount() { return exitCount; }
    public void setExitCount(Integer exitCount) { this.exitCount = exitCount; }

    public Integer getGasAlerts() { return gasAlerts; }
    public void setGasAlerts(Integer gasAlerts) { this.gasAlerts = gasAlerts; }

    public Integer getSmokeAlerts() { return smokeAlerts; }
    public void setSmokeAlerts(Integer smokeAlerts) { this.smokeAlerts = smokeAlerts; }

    public Integer getTempAlerts() { return tempAlerts; }
    public void setTempAlerts(Integer tempAlerts) { this.tempAlerts = tempAlerts; }

    public Integer getLightTriggers() { return lightTriggers; }
    public void setLightTriggers(Integer lightTriggers) { this.lightTriggers = lightTriggers; }

    public Integer getLeakAlerts() { return leakAlerts; }
    public void setLeakAlerts(Integer leakAlerts) { this.leakAlerts = leakAlerts; }

    public Integer getCamPositive() { return camPositive; }
    public void setCamPositive(Integer camPositive) { this.camPositive = camPositive; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime периодEnd) { this.periodEnd = периодEnd; }
}