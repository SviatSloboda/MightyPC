package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.Specs;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record Workstation(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        Specs specs,
        int cpuNumber,
        int gpuNumber,
        List<String> photos
) {
    public Workstation(String id, HardwareSpec hardwareSpec, Specs specs, int cpuNumber, int gpuNumber) {
        this(id, hardwareSpec, specs, cpuNumber, gpuNumber, new ArrayList<>(Collections.emptyList()));
    }

    public Workstation(HardwareSpec hardwareSpec, Specs specs, int cpuNumber, int gpuNumber){
        this(UUID.randomUUID().toString(), hardwareSpec, specs, cpuNumber, gpuNumber);
    }

    public Workstation withPhotos(List<String> allPhotos) {
        return new Workstation(id(), hardwareSpec(), specs(), cpuNumber(), gpuNumber(), allPhotos);
    }
}
