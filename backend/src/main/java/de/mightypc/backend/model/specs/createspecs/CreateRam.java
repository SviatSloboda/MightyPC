package de.mightypc.backend.model.specs.createspecs;

public record CreateRam(
        String name,
        String description,
        String type,
        int energyConsumption,
        int memorySize,
        float price,
        float rating
) {
}
