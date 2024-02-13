package de.mightypc.backend.model.specs.createspecs;

public record CreatePowerSupply(
        CreateHardwareSpec hardwareSpec,
        int power
) {
}
