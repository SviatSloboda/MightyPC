package de.mightypc.backend.model.hardware;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
public record HDD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption,
        List<String> hddPhotos
) {
    public HDD(String id, HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(id, hardwareSpec, capacity, energyConsumption, new ArrayList<>());
    }

    public HDD(HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, capacity, energyConsumption, new ArrayList<>());
    }

    public HDD withPhotos(List<String> allPhotos) {
        return new HDD(id(), hardwareSpec(), capacity(), energyConsumption(), allPhotos);
    }
}
