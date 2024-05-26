package de.mightypc.backend.service.configurator;

import de.mightypc.backend.model.configurator.ConfiguratorItems;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.SsdService;
import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public String getAllComponentsIdsAndNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(cpuService.getAllNamesWithPrices());
        stringBuilder.append(gpuService.getAllNamesWithPrices());
        stringBuilder.append(motherboardService.getAllNamesWithPrices());
        stringBuilder.append(ramService.getAllNamesWithPrices());
        stringBuilder.append(ssdService.getAllNamesWithPrices());
        stringBuilder.append(hddService.getAllNamesWithPrices());
        stringBuilder.append(powerSupplyService.getAllNamesWithPrices());
        stringBuilder.append(pcCaseService.getAllNamesWithPrices());

        return new String(stringBuilder).replace(" ", "");
    }

    public ConfiguratorItems getAllItems() {
        List<List<ItemForConfigurator>> allItems = new ArrayList<>();

        allItems.add(cpuService.getAllHardwareInfoForConfiguration());
        allItems.add(gpuService.getAllHardwareInfoForConfiguration());
        allItems.add(motherboardService.getAllHardwareInfoForConfiguration());
        allItems.add(ramService.getAllHardwareInfoForConfiguration());
        allItems.add(ssdService.getAllHardwareInfoForConfiguration());
        allItems.add(hddService.getAllHardwareInfoForConfiguration());
        allItems.add(powerSupplyService.getAllHardwareInfoForConfiguration());
        allItems.add(pcCaseService.getAllHardwareInfoForConfiguration());

        return new ConfiguratorItems(allItems);
    }

    public boolean validateComponentIds(String[] ids) {
        Set<String> validIds = getAllValidComponentIds();
        for (String id : ids) {
            if (!validIds.contains(id)) {
                return false;
            }
        }
        return true;
    }

    private Set<String> getAllValidComponentIds() {
        Set<String> validIds = new HashSet<>();

        validIds.addAll(cpuService.getAllIds());
        validIds.addAll(gpuService.getAllIds());
        validIds.addAll(motherboardService.getAllIds());
        validIds.addAll(ramService.getAllIds());
        validIds.addAll(ssdService.getAllIds());
        validIds.addAll(hddService.getAllIds());
        validIds.addAll(powerSupplyService.getAllIds());
        validIds.addAll(pcCaseService.getAllIds());

        return validIds;
    }

}
