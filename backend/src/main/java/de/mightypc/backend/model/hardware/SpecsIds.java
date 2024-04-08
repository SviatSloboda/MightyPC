package de.mightypc.backend.model.hardware;

public record SpecsIds(
        String cpuId,
        String gpuId,
        String motherboardId,
        String ramId,
        String ssdId,
        String hddId,
        String powerSupplyId,
        String pcCaseId
) {
}