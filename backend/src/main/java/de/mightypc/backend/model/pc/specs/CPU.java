package de.mightypc.backend.model.pc.specs;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
public record CPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket,
        List<String> cpuPhotos
) {
    public CPU(String id, HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(id, hardwareSpec, energyConsumption, socket, new ArrayList<>());
    }

    public CPU(HardwareSpec hardwareSpec, int energyConsumption, String socket) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption, socket, new ArrayList<>());
    }

    public CPU withPhotos(List <String> allPhotos) {
        return new CPU(id(), hardwareSpec(), energyConsumption(), socket(), allPhotos);
    }
}
