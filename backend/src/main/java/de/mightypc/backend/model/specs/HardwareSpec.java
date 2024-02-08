package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

public record HardwareSpec(
        @Id
        String id,
        String name,
        String description,
        BigDecimal price,
        float rating
) {
    public HardwareSpec(String name, String description, BigDecimal price, float rating) {
        this(UUID.randomUUID().toString(), name, description, price, rating);
    }
}
