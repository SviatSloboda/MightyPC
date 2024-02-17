package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        this(id, hardwareSpec, type, energyConsumption, memorySize, Collections.emptyList());
    }

    public RAM(HardwareSpec hardwareSpec, String type, int energyConsumption, int memorySize) {
        this(UUID.randomUUID().toString(), hardwareSpec, type, energyConsumption, memorySize, Collections.emptyList());
    }

    public RAM withPhotos(List<String> allPhotos) {
        return new RAM(id(), hardwareSpec(), type(), memorySize(), energyConsumption(), allPhotos);
    }
}
