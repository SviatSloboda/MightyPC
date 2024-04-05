package de.mightypc.backend.service.pc.configurator;

import de.mightypc.backend.model.pc.ConfiguratorComponents;
import de.mightypc.backend.service.pc.hardware.CpuService;
import de.mightypc.backend.service.pc.hardware.GpuService;
import de.mightypc.backend.service.pc.hardware.HddService;
import de.mightypc.backend.service.pc.hardware.MotherboardService;
import de.mightypc.backend.service.pc.hardware.PcCaseService;
import de.mightypc.backend.service.pc.hardware.PowerSupplyService;
import de.mightypc.backend.service.pc.hardware.RamService;
import de.mightypc.backend.service.pc.hardware.SsdService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConfiguratorService {
    private final CpuService cpuService;
    private final GpuService gpuService;
    private final MotherboardService motherboardService;
    private final SsdService ssdService;
    private final HddService hddService;
    private final RamService ramService;
    private final PcCaseService pcCaseService;
    private final PowerSupplyService powerSupplyService;

    public ConfiguratorService(CpuService cpuService, GpuService gpuService, SsdService ssdService, HddService hddService, RamService ramService, PcCaseService pcCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        this.cpuService = cpuService;
        this.gpuService = gpuService;
        this.motherboardService = motherboardService;
        this.ssdService = ssdService;
        this.hddService = hddService;
        this.ramService = ramService;
        this.pcCaseService = pcCaseService;
        this.powerSupplyService = powerSupplyService;
    }

    public ConfiguratorComponents getAllComponentsIdsAndNamesWithPrices() {
        List<Map<String, String>> allComponentsIdsAndNames = new ArrayList<>();

        allComponentsIdsAndNames.add(cpuService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(gpuService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(motherboardService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(ramService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(ssdService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(hddService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(powerSupplyService.getAllNamesWithPrices());
        allComponentsIdsAndNames.add(pcCaseService.getAllNamesWithPrices());

        return new ConfiguratorComponents(allComponentsIdsAndNames);
    }
}
