package de.mightypc.backend.model.specs;

import java.util.Arrays;
import java.util.Objects;

public record Motherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        GPU[] graphicCardCompatibility,
        CPU[] processorCompatibility
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motherboard that = (Motherboard) o;
        return energyConsumption == that.energyConsumption && Objects.equals(hardwareSpec, that.hardwareSpec) && Arrays.equals(graphicCardCompatibility, that.graphicCardCompatibility) && Arrays.equals(processorCompatibility, that.processorCompatibility);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hardwareSpec, energyConsumption);
        result = 31 * result + Arrays.hashCode(graphicCardCompatibility);
        result = 31 * result + Arrays.hashCode(processorCompatibility);
        return result;
    }
}
