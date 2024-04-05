package de.mightypc.backend.model.pc.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record Motherboard(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket,
        List<String> motherboardPhotos

) {
    public Motherboard(String id, HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(id, hardwareSpec, energyConsumption, socket, Collections.emptyList());
    }

    public Motherboard(HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption, socket, Collections.emptyList());
    }

    public Motherboard withPhotos(List<String> allPhotos) {
        return new Motherboard(id(), hardwareSpec(), energyConsumption(), socket(), allPhotos);
    }
}
