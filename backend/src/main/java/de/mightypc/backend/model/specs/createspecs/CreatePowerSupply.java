package de.mightypc.backend.model.specs.createspecs;

public record CreatePowerSupply(
        String name,
        String description,
        int power,
        float price,
        float rating
) {
}
