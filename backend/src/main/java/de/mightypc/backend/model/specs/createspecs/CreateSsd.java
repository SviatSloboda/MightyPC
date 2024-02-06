package de.mightypc.backend.model.specs.createspecs;

public record CreateSsd(
        String name,
        String description,
        int energyConsumption,
        float price
) {
}
