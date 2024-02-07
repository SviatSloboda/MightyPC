package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record GPU(
        @Id
        String id,
        String name,
        String description,
        float price,
        int performance,
        int energyConsumption,
        float rating
) {
    public GPU(String name, String description, float price, int performance, int energyConsumption, float rating) {
        this(UUID.randomUUID().toString(), name, description, price, performance, energyConsumption, rating);
    }
}
