package de.mightypc.backend.model.specs.createspecs;

public record CreateHdd(
        CreateHardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
}
