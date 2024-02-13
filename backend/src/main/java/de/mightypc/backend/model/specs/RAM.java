package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record RAM(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
    public RAM(HardwareSpec hardwareSpec, String type, int energyConsumption, int memorySize) {
        this(UUID.randomUUID().toString(), hardwareSpec, type, energyConsumption, memorySize);
    }
}
