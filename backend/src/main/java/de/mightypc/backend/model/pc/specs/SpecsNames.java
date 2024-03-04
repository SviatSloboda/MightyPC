package de.mightypc.backend.model.pc.specs;

public record SpecsNames(
        String cpuName,
        String gpuName,
        String motherboardName,
        String ramName,
        String ssdName,
        String hddName,
        String powerSupplyName,
        String pcCaseName
) {
}
