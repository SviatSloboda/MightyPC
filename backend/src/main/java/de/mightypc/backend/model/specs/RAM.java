package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record RAM(
        @Id
        String id,
        String name,
        String description,
        String type,
        int energyConsumption,
        int memorySize,
        float price
) {
    public RAM(String name, String description, String type, int energyConsumption, int memorySize, float price){
        this(UUID.randomUUID().toString(), name, description, type, energyConsumption, memorySize, price);
    }
}
