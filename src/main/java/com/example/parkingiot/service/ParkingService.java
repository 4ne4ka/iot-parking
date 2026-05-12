package com.example.parkingiot.service;

import com.example.parkingiot.model.*;
import com.example.parkingiot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ParkingService {

    private final SensorEventRepository sensorEventRepository;
    private final ParkingStateRepository parkingStateRepository;
    private final CommandLogRepository commandLogRepository;
    private final AggregatedDataRepository aggregatedDataRepository;

    private static final int TOTAL_SPACES = 100;
    private static final int COOLDOWN_SECONDS = 20; // задержка между повторными командами

    public ParkingService(SensorEventRepository sensorEventRepository,
                          ParkingStateRepository parkingStateRepository,
                          CommandLogRepository commandLogRepository,
                          AggregatedDataRepository aggregatedDataRepository) {
        this.sensorEventRepository = sensorEventRepository;
        this.parkingStateRepository = parkingStateRepository;
        this.commandLogRepository = commandLogRepository;
        this.aggregatedDataRepository = aggregatedDataRepository;

        // Инициализация: если база пустая, создаем начальное состояние
        if (parkingStateRepository.findFirstByOrderByIdDesc().isEmpty()) {
            parkingStateRepository.save(new ParkingState());
        }
    }

    /**
     * Главный метод приема данных от Node-RED
     */
    @Transactional
    public void processSensorData(Map<String, Integer> data) {
        ParkingState state = getCurrentState();
        AggregatedData period = getCurrentPeriod();

        // Обрабатываем каждый датчик
        if (data.containsKey("EntryLoop")) {
            int val = data.get("EntryLoop");
            sensorEventRepository.save(new SensorEvent("EntryLoop", val));
            period.setEntryCount(period.getEntryCount() + val);

            if (val == 1) {
                state.setEntryCount(state.getEntryCount() + 1);
                state.setOccupiedSpaces(Math.min(TOTAL_SPACES, state.getOccupiedSpaces() + 1));

                // Шлагбаум въезда открыт, если есть места
                if (state.getFreeSpaces() > 0 && !state.getEntryBarrierOpen()) {
                    state.setEntryBarrierOpen(true);
                    commandLogRepository.save(new CommandLog(
                            "EntryBarrier", "open", "automatic", "Vehicle detected and space available"
                    ));
                }
            } else {
                // Машина проехала — закрываем шлагбаум
                if (state.getEntryBarrierOpen()) {
                    state.setEntryBarrierOpen(false);
                    commandLogRepository.save(new CommandLog(
                            "EntryBarrier", "close", "automatic", "Vehicle passed"
                    ));
                }
            }
        }

        if (data.containsKey("ExitLoop")) {
            int val = data.get("ExitLoop");
            sensorEventRepository.save(new SensorEvent("ExitLoop", val));
            period.setExitCount(period.getExitCount() + val);

            if (val == 1) {
                state.setExitCount(state.getExitCount() + 1);
                state.setOccupiedSpaces(Math.max(0, state.getOccupiedSpaces() - 1));

                if (!state.getExitBarrierOpen()) {
                    state.setExitBarrierOpen(true);
                    commandLogRepository.save(new CommandLog(
                            "ExitBarrier", "open", "automatic", "Vehicle exiting"
                    ));
                }
            } else {
                if (state.getExitBarrierOpen()) {
                    state.setExitBarrierOpen(false);
                    commandLogRepository.save(new CommandLog(
                            "ExitBarrier", "close", "automatic", "Vehicle exited"
                    ));
                }
            }
        }

        // Газовый датчик → вентиляция
        if (data.containsKey("GasSensor")) {
            int val = data.get("GasSensor");
            sensorEventRepository.save(new SensorEvent("GasSensor", val));
            period.setGasAlerts(period.getGasAlerts() + val);

            if (val == 1 && canTrigger(state, "Ventilation")) {
                state.setVentilationOn(true);
                commandLogRepository.save(new CommandLog(
                        "Ventilation", "on", "automatic", "High CO/NO₂ level detected"
                ));
            } else if (val == 0 && state.getVentilationOn() && canTrigger(state, "Ventilation")) {
                state.setVentilationOn(false);
                commandLogRepository.save(new CommandLog(
                        "Ventilation", "off", "automatic", "Air quality normalized"
                ));
            }
        }

        // Датчик дыма → вентиляция (дублирует команду)
        if (data.containsKey("SmokeSensor")) {
            int val = data.get("SmokeSensor");
            sensorEventRepository.save(new SensorEvent("SmokeSensor", val));
            period.setSmokeAlerts(period.getSmokeAlerts() + val);

            if (val == 1 && !state.getVentilationOn()) {
                state.setVentilationOn(true);
                commandLogRepository.save(new CommandLog(
                        "Ventilation", "on", "automatic", "Smoke detected"
                ));
            }
        }

        // Датчик температуры → вентиляция
        if (data.containsKey("TempSensor")) {
            int val = data.get("TempSensor");
            sensorEventRepository.save(new SensorEvent("TempSensor", val));
            period.setTempAlerts(period.getTempAlerts() + val);

            if (val == 1 && !state.getVentilationOn()) {
                state.setVentilationOn(true);
                commandLogRepository.save(new CommandLog(
                        "Ventilation", "on", "automatic", "High temperature detected"
                ));
            }
        }

        // Датчик освещенности → освещение
        if (data.containsKey("LightSensor")) {
            int val = data.get("LightSensor");
            sensorEventRepository.save(new SensorEvent("LightSensor", val));
            period.setLightTriggers(period.getLightTriggers() + val);

            if (val == 1 && !state.getLightingOn()) {
                state.setLightingOn(true);
                commandLogRepository.save(new CommandLog(
                        "Lighting", "on", "automatic", "Low light level"
                ));
            } else if (val == 0 && state.getLightingOn() && canTrigger(state, "Lighting")) {
                state.setLightingOn(false);
                commandLogRepository.save(new CommandLog(
                        "Lighting", "off", "automatic", "Sufficient light"
                ));
            }
        }

        // Датчик протечки → насос
        if (data.containsKey("LeakSensor")) {
            int val = data.get("LeakSensor");
            sensorEventRepository.save(new SensorEvent("LeakSensor", val));
            period.setLeakAlerts(period.getLeakAlerts() + val);

            if (val == 1 && !state.getDrainPumpOn()) {
                state.setDrainPumpOn(true);
                commandLogRepository.save(new CommandLog(
                        "DrainPump", "on", "automatic", "Water leak detected"
                ));
            } else if (val == 0 && state.getDrainPumpOn() && canTrigger(state, "DrainPump")) {
                state.setDrainPumpOn(false);
                commandLogRepository.save(new CommandLog(
                        "DrainPump", "off", "automatic", "No leak"
                ));
            }
        }

        // Камера / считыватель
        if (data.containsKey("HybridReader")) {
            int val = data.get("HybridReader");
            sensorEventRepository.save(new SensorEvent("HybridReader", val));
            period.setCamPositive(period.getCamPositive() + val);
        }

        state.setUpdatedAt(LocalDateTime.now());
        parkingStateRepository.save(state);
        aggregatedDataRepository.save(period);
    }

    /**
     * Получить текущее состояние для API
     */
    public Map<String, Object> getFullStatus() {
        ParkingState state = getCurrentState();

        Map<String, Object> status = new HashMap<>();
        status.put("parking", Map.of(
                "totalSpaces", state.getTotalSpaces(),
                "occupiedSpaces", state.getOccupiedSpaces(),
                "freeSpaces", state.getFreeSpaces(),
                "entryCount", state.getEntryCount(),
                "exitCount", state.getExitCount()
        ));
        status.put("systems", Map.of(
                "ventilation", state.getVentilationOn() ? "on" : "off",
                "lighting", state.getLightingOn() ? "on" : "off",
                "entryBarrier", state.getEntryBarrierOpen() ? "open" : "closed",
                "exitBarrier", state.getExitBarrierOpen() ? "open" : "closed",
                "drainPump", state.getDrainPumpOn() ? "on" : "off"
        ));

        // Добавляем последние события
        status.put("recentEvents", sensorEventRepository.findAll()
                .stream()
                .limit(20)
                .toList());

        status.put("recentCommands", commandLogRepository.findTop20ByOrderByCreatedAtDesc());
        status.put("timestamp", LocalDateTime.now().toString());

        return status;
    }

    private ParkingState getCurrentState() {
        return parkingStateRepository.findFirstByOrderByIdDesc()
                .orElse(new ParkingState());
    }

    private AggregatedData getCurrentPeriod() {
        return aggregatedDataRepository.findFirstByOrderByPeriodStartDesc()
                .orElse(new AggregatedData());
    }

    // Проверка на кулдаун (чтобы не дергать устройства 10 раз в секунду)
    private boolean canTrigger(ParkingState state, String device) {
        // Упрощенная версия: всегда разрешаем
        // В реальной системе смотрели бы на время последней команды
        return true;
    }
}