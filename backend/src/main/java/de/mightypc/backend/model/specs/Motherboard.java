package de.mightypc.backend.model.specs;

public record Motherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        GPU[] graphicCardCompatibility,
        CPU[] processorCompatibility
) {
}
