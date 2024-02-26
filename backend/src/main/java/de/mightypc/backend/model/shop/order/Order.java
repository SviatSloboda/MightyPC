package de.mightypc.backend.model.shop;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Order(
        @Id
        String id,
        List<Item> items,
        BigDecimal completePrice,

) {
    public Order(List<Item> items, BigDecimal completePrice) {
        this(UUID.randomUUID().toString(), items, completePrice);
    }
}
