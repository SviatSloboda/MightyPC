package de.mightypc.backend.model.specs;

public record CPU(
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
}
