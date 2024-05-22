package de.mightypc.backend.model.shop.order;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

public record Item(
        @Id
        String id,
        String name,
        String description,
        BigDecimal price,
        String photo,
        String pathToCharacteristicsPage
) {
}
