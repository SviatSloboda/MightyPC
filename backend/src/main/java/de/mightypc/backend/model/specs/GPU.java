package de.mightypc.backend.model.specs;

public record GPU(
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {

}
