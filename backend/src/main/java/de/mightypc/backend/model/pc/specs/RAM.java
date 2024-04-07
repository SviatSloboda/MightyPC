package de.mightypc.backend.model.pc.specs;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@With
public record RAM(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize,
        List<String> ramPhotos
) {
    public RAM(String id, HardwareSpec hardwareSpec, String type, int energyConsumption, int memorySize) {
        this(id, hardwareSpec, type, energyConsumption, memorySize, new ArrayList<>());
    }

    public RAM(HardwareSpec hardwareSpec, String type, int energyConsumption, int memorySize) {
        this(UUID.randomUUID().toString(), hardwareSpec, type, energyConsumption, memorySize, new ArrayList<>());
    }

    public RAM withPhotos(List<String> allPhotos) {
        return new RAM(id(), hardwareSpec(), type(), energyConsumption(), memorySize(), allPhotos);
    }
}
