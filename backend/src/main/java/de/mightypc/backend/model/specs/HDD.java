package de.mightypc.backend.model.specs;

public record HDD(
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
}
