package de.mightypc.backend.model.pc.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Arrays;


public record Motherboard(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String[] graphicCardCompatibility,
        String[] processorCompatibility,
        List<String> motherboardPhotos

) {
    public Motherboard(String id, HardwareSpec hardwareSpec, int energyConsumption, String[] graphicCardCompatibility, String[] processorCompatibility) {
        this(id, hardwareSpec, energyConsumption, graphicCardCompatibility, processorCompatibility, Collections.emptyList());
    }

    public Motherboard(HardwareSpec hardwareSpec, int energyConsumption, String[] graphicCardCompatibility, String[] processorCompatibility) {
        this(UUID.randomUUID().toString(), hardwareSpec, energyConsumption, graphicCardCompatibility, processorCompatibility, Collections.emptyList());
    }

    public Motherboard withPhotos(List<String> allPhotos) {
        return new Motherboard(id(), hardwareSpec(), energyConsumption(), graphicCardCompatibility(), processorCompatibility(), allPhotos);
    }

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
