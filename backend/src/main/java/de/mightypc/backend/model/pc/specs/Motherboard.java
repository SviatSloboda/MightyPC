package de.mightypc.backend.model.pc.specs;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@With
public record Motherboard(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket,
        List<String> motherboardPhotos
) {
    public Motherboard(String id, HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(id, hardwareSpec, energyConsumption, socket, new ArrayList<>());
    }

    public Motherboard(HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption, socket, new ArrayList<>());
    }

    public Motherboard withPhotos(List<String> allPhotos) {
        return new Motherboard(id(), hardwareSpec(), energyConsumption(), socket(), allPhotos);
    }
}
