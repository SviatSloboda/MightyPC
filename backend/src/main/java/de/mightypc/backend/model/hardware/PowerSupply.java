package de.mightypc.backend.model.hardware;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@With
public record PowerSupply(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int power,
        List<String> powerSupplyPhotos

) {
    public PowerSupply(String id, HardwareSpec hardwareSpec, int power) {
        this(id, hardwareSpec, power, new ArrayList<>());
    }

    public PowerSupply(HardwareSpec hardwareSpec, int power) {
        this(UUID.randomUUID().toString(), hardwareSpec, power, new ArrayList<>());
    }

    public PowerSupply withPhotos(List<String> allPhotos) {
        return new PowerSupply(id(), hardwareSpec(), power(), allPhotos);
    }
}
