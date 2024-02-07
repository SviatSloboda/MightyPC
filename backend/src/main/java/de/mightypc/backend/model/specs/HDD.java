package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record HDD(
        @Id
        String id,
        String name,
        String description,
        int capacity,
        int energyConsumption,
        float price,
        float rating
) {
    public HDD(String name, String description, int capacity, int energyConsumption, float price, float rating) {
        this(UUID.randomUUID().toString(), name, description, capacity, energyConsumption, price, rating);
    }
}
