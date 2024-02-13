package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record CPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
    public CPU(HardwareSpec hardwareSpec, int performance, int energyConsumption){
        this(UUID.randomUUID().toString(), hardwareSpec, performance, energyConsumption);
    }
}
