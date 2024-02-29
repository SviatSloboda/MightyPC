package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.PcCase;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

public record CreateWorkstation(@Id String id, String name, Specs specs, PcCase pcCase, int cpuNumber, int gpuNumber,
                                BigDecimal totalPrice, List<String> photos) {
}
