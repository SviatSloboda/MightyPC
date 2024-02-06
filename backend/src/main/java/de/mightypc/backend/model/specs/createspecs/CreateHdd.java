package de.mightypc.backend.model.specs.createspecs;

public record CreateHdd(
        String name,
        String description,
        int capacity,
        int energyConsumption,
        float price
) {
}
