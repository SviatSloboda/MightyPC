package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Specs;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@With
public record Workstation(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        Specs specs,
        int cpuNumber,
        int gpuNumber,
        int energyConsumption,
        List<String> photos
) {
    public Workstation(String id, HardwareSpec hardwareSpec, Specs specs, int cpuNumber, int gpuNumber, int energyConsumption) {
        this(id, hardwareSpec, specs, cpuNumber, gpuNumber, energyConsumption, new ArrayList<>(Collections.emptyList()));
    }

    public Workstation(HardwareSpec hardwareSpec, Specs specs, int cpuNumber, int gpuNumber, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, specs, cpuNumber, gpuNumber, energyConsumption);
    }
}
