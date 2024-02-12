package de.mightypc.backend.model.specs.createspecs;

public record CreateMotherboard(
        CreateHardwareSpec hardwareSpec,
        int energyConsumption,
        String[] graphicCardCompatibility,
        String[] processorCompatibility
) {
}
