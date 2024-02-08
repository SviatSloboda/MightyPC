package de.mightypc.backend.model.specs.createspecs;

public record CreateCpu (
    String name,
    String description,
    float price,
    int performance,
    int energyConsumption,
    float rating
){
}
