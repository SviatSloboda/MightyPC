package de.mightypc.backend.model.specs;

import java.util.Arrays;
import java.util.Objects;

public record Motherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String[] graphicCardCompatibility,
        String[] processorCompatibility
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motherboard that = (Motherboard) o;
        return energyConsumption == that.energyConsumption &&
               Objects.equals(hardwareSpec, that.hardwareSpec) &&
               Arrays.deepEquals(graphicCardCompatibility, that.graphicCardCompatibility) &&
               Arrays.deepEquals(processorCompatibility, that.processorCompatibility);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hardwareSpec, energyConsumption);
        result = 31 * result + Arrays.deepHashCode(graphicCardCompatibility);
        result = 31 * result + Arrays.deepHashCode(processorCompatibility);
        return result;
    }

    @Override
    public String toString() {
        return "Motherboard{" +
               "hardwareSpec=" + hardwareSpec +
               ", energyConsumption=" + energyConsumption +
               ", graphicCardCompatibility=" + Arrays.toString(graphicCardCompatibility) +
               ", processorCompatibility=" + Arrays.toString(processorCompatibility) +
               '}';
    }
}
