package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.PcCase;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

public record PC(
        @Id
        String id,
        String name,
        Specs specs,
        PcCase pcCase,
        BigDecimal price
) {
    public PC(String name, Specs specs, PcCase pcCase, BigDecimal price){
        this(UUID.randomUUID().toString(), name, specs, pcCase, price);
    }
}
