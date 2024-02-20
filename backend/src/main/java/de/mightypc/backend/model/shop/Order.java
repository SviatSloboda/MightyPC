package de.mightypc.backend.model.shop;

import java.math.BigDecimal;
import java.util.List;

public record Order(
        String id,
        BigDecimal completePrice,
        List<Item> items
) {
}
