package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HardwareSpec that = (HardwareSpec) o;
        return Float.compare(rating, that.rating) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price, rating);
    }
}
