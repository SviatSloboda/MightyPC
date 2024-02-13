package de.mightypc.backend.model.specs;

public record RAM(
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
}
