package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record SSD(
        @Id
        String id,
        String name,
        String description,
        int energyConsumption,
        float price
) {
    public SSD(String name, String description, int energyConsumption, float price){
        this(UUID.randomUUID().toString(), name, description, energyConsumption, price);
    }
}
