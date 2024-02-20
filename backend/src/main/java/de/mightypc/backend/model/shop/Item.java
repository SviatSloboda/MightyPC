package de.mightypc.backend.model.shop;

import java.math.BigDecimal;
import java.util.List;

public record Item(
        String id,
        String name,
        BigDecimal price,
        List<String> photos
) {
}
