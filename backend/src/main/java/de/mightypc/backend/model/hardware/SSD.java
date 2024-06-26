package de.mightypc.backend.model.hardware;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
public record SSD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption,
        List<String> ssdPhotos
) {
    public SSD(String id, HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(id, hardwareSpec, capacity, energyConsumption, new ArrayList<>());
    }

    public SSD(HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, capacity, energyConsumption, new ArrayList<>());
    }

    public SSD withPhotos(List<String> allPhotos) {
        return new SSD(id(), hardwareSpec(), capacity(), energyConsumption(), allPhotos);
    }
}
