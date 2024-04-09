package de.mightypc.backend.model.configurator;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.SSD;

public record SpecsForEnergyConsumption(
        CPU cpu,
        GPU gpu,
        Motherboard motherboard,
        RAM ram,
        SSD ssd,
        HDD hdd
)  {
}
