package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record GPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
    public GPU(HardwareSpec hardwareSpec, int performance, int energyConsumption){
        this(UUID.randomUUID().toString(), hardwareSpec, performance, energyConsumption);
    }
}
