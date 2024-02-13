package de.mightypc.backend.model.specs.createspecs;

public record CreateCpu(
        CreateHardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
}
