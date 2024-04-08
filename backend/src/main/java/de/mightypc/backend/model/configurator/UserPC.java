package de.mightypc.backend.model.configurator;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Specs;
import org.springframework.data.annotation.Id;

import java.util.List;

public record UserPC(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        Specs specs,
        List<String> photos
) {

}
