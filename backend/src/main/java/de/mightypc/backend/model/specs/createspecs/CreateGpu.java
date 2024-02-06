package de.mightypc.backend.model.specs.createspecs;

public record CreateGpu (
        String name,
        String description,
        float price,
        int performance,
        int energyConsumption,
        double rating
){
}
