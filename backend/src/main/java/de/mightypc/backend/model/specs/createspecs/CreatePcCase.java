package de.mightypc.backend.model.specs.createspecs;

public record CreatePcCase (
        CreateHardwareSpec hardwareSpec,
        String dimensions
){
}
