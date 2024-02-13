package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreatePcCase (
        HardwareSpec hardwareSpec,
        String dimensions
){
}
