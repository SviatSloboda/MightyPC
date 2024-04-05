package de.mightypc.backend.model.pc.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record CPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption,
        String socket,
        List<String> cpuPhotos
) {
    public CPU(String id, HardwareSpec hardwareSpec, int performance, int energyConsumption, String socket) {
        this(id, hardwareSpec, performance, energyConsumption, socket, Collections.emptyList());
    }

    public CPU(HardwareSpec hardwareSpec, int performance, int energyConsumption, String socket) {
        this(UUID.randomUUID().toString(), hardwareSpec, performance, energyConsumption, socket, Collections.emptyList());
    }

    public CPU withPhotos(List<String> allPhotos) {
        return new CPU(id(), hardwareSpec(), performance(), energyConsumption(), socket(), allPhotos);
    }
}
