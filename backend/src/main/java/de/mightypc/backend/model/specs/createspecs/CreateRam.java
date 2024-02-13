package de.mightypc.backend.model.specs.createspecs;

public record CreateRam(
        CreateHardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
}
