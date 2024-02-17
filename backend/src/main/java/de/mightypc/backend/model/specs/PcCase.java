package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record PcCase(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        String dimensions
) {
    public PcCase(HardwareSpec hardwareSpec, String dimensions) {
        this(UUID.randomUUID().toString(), hardwareSpec, dimensions);
    }
}
