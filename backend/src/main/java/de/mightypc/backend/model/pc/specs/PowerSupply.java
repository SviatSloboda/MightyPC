package de.mightypc.backend.model.pc.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record PowerSupply(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int power,
        List<String> powerSupplyPhotos

) {
    public PowerSupply(String id, HardwareSpec hardwareSpec, int power) {
        this(id, hardwareSpec, power, Collections.emptyList());
    }

    public PowerSupply(HardwareSpec hardwareSpec, int power) {
        this(UUID.randomUUID().toString(), hardwareSpec, power, Collections.emptyList());
    }

    public PowerSupply withPhotos(List<String> allPhotos) {
        return new PowerSupply(id(), hardwareSpec(), power(), allPhotos);
    }
}
