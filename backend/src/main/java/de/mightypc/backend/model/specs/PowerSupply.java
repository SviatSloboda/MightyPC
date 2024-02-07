package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record PowerSupply(
        @Id
        String id,
        String name,
        String description,
        int power,
        float price,
        float rating
) {
    public PowerSupply(String name, String description, int power, float price, float rating){
        this(UUID.randomUUID().toString(), name, description, power, price, rating);
    }
}
