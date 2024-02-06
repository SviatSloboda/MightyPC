package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record CPU(
        @Id
        String id,
        String name,
        String description,
        float price,
        int performance,
        int energyConsumption,
        double rating
) {
    public CPU(String name, String description, float price, int performance, int energyConsumption, double rating) {
        this(UUID.randomUUID().toString(), name, description, price, performance, energyConsumption, rating);
    }
}
