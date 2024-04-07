package de.mightypc.backend.model.pc.specs;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
public record GPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption,
        List<String> gpuPhotos
) {
    public GPU(String id, HardwareSpec hardwareSpec, int energyConsumption) {
        this(id, hardwareSpec, energyConsumption, new ArrayList<>());
    }

    public GPU(HardwareSpec hardwareSpec, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption, new ArrayList<>());
    }

    public GPU withPhotos(List<String> allPhotos) {
        return new GPU(id(), hardwareSpec(), energyConsumption(), allPhotos);
    }
}
