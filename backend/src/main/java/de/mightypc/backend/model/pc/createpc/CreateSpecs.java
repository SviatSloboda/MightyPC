package de.mightypc.backend.model.pc.createpc;

public record CreateSpecs(String cpuId, String gpuId, String motherboardId, String ramId, String ssdId, String hddId,
                          String powerSupplyId, String pcCaseId) {
}
