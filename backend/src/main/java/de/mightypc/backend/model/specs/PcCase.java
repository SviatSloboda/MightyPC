package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record PcCase(
        @Id
        String id,
        String name,
        float price,
        float rating
) {
    public PcCase(String name, float price, float rating){
        this(UUID.randomUUID().toString(), name, price, rating);
    }
}
