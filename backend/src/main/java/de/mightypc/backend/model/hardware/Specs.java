package de.mightypc.backend.model.hardware;

public record Specs(
        CPU cpu,
        GPU gpu,
        Motherboard motherboard,
        RAM ram,
        SSD ssd,
        HDD hdd,
        PowerSupply powerSupply,
        PcCase pcCase
) {
}
