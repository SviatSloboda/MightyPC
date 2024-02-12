package de.mightypc.backend.model.specs.createspecs;

public record CreateGpu (
        CreateHardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
){
}
