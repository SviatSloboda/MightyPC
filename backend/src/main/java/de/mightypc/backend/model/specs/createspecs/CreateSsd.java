package de.mightypc.backend.model.specs.createspecs;

public record CreateSsd(
        CreateHardwareSpec hardwareSpec,
        int energyConsumption
) {
}
