package de.mightypc.backend.model.shop;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

public record Item(
        @Id
        String id,
        String userId,
        String name,
        BigDecimal price,
        List<String> photos
) {
}
