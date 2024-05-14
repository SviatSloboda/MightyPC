package de.mightypc.backend.model.shop.order;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

public record Item(
        @Id
        String id,
        String type,
        String name,
        String description,
        BigDecimal price,
        List<String> photos
) {
}
