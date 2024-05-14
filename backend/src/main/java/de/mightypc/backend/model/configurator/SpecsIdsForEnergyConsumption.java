package de.mightypc.backend.model.configurator;

public record SpecsIdsForEnergyConsumption(
        String cpuId,
        String gpuId,
        String motherboardId,
        String ramId,
        String ssdId,
        String hddId
) {
}