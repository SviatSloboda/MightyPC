package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record SSD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption
) {
    public SSD(HardwareSpec hardwareSpec, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption);
    }
}
