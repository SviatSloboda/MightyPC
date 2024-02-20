package de.mightypc.backend.model.pc.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record PcCase(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        String dimensions,
        List<String> pcCasePhotos

) {
    public PcCase(String id, HardwareSpec hardwareSpec, String dimensions) {
        this(id, hardwareSpec, dimensions, Collections.emptyList());
    }

    public PcCase(HardwareSpec hardwareSpec, String dimensions) {
        this(UUID.randomUUID().toString(), hardwareSpec, dimensions, Collections.emptyList());
    }

    public PcCase withPhotos(List<String> allPhotos) {
        return new PcCase(id(), hardwareSpec(), dimensions(), allPhotos);
    }
}
