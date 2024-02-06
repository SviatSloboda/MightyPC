package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record Motherboard(
        @Id
        String id,
        String name,
        String description,
        float price,
        int energyConsumption,
        GPU[] graphicCardCompatibility,
        CPU[] processorCompatibility
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motherboard that = (Motherboard) o;
        return price == that.price && energyConsumption == that.energyConsumption && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Arrays.equals(graphicCardCompatibility, that.graphicCardCompatibility) && Arrays.equals(processorCompatibility, that.processorCompatibility);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, description, price, energyConsumption);
        result = 31 * result + Arrays.hashCode(graphicCardCompatibility);
        result = 31 * result + Arrays.hashCode(processorCompatibility);
        return result;
    }

    @Override
    public String toString() {
        return "Motherboard{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", energyConsumption=" + energyConsumption +
               ", graphicCardCompatibility=" + Arrays.toString(graphicCardCompatibility) +
               ", processorCompatibility=" + Arrays.toString(processorCompatibility) +
               '}';
    }

    public Motherboard(String name, String description, float price, int energyConsumption, GPU[] graphicCardCompatibility, CPU[] processorCompatibility){
        this(UUID.randomUUID().toString(), name, description, price, energyConsumption, graphicCardCompatibility, processorCompatibility);
    }
}
