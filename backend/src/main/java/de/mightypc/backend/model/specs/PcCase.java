package de.mightypc.backend.model.specs;

public record PcCase(
        HardwareSpec hardwareSpec,
        int height,
        int width,
        int length
) {
}
