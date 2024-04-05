package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.Specs;
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
