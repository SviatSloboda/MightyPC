package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record PowerSupply(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int power
) {
    public PowerSupply(HardwareSpec hardwareSpec, int power) {
        this(UUID.randomUUID().toString(), hardwareSpec, power);
    }
}
