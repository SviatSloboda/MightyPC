package de.mightypc.backend.model;

import de.mightypc.backend.model.specs.PcCase;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public record PC(
        @Id
        String id,
        String name,
        Specs specs,
        PcCase pcCase,
        float price
) {
    public PC(String name, Specs specs, PcCase pcCase, float price){
        this(UUID.randomUUID().toString(), name, specs, pcCase, price);
    }
}
