package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.PcNotFoundException;
import de.mightypc.backend.model.configurator.SpecsForEnergyConsumption;
import de.mightypc.backend.model.configurator.SpecsIdsForEnergyConsumption;
import de.mightypc.backend.model.hardware.Specs;
import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.RamService;
import de.mightypc.backend.service.hardware.SsdService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class PcBaseService<T, R extends MongoRepository<T, String>> {
    private final CpuService cpuService;
    private final GpuService gpuService;
    private final SsdService ssdService;
    private final HddService hddService;
    private final RamService ramService;
    private final PcCaseService pcCaseService;
    private final PowerSupplyService powerSupplyService;
    private final MotherboardService motherboardService;
    protected R repository;

    protected PcBaseService(R repository, CpuService cpuService, GpuService gpuService, SsdService ssdService, HddService hddService, RamService ramService, PcCaseService pcCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        this.repository = repository;
        this.cpuService = cpuService;
        this.gpuService = gpuService;
        this.ssdService = ssdService;
        this.hddService = hddService;
        this.ramService = ramService;
        this.pcCaseService = pcCaseService;
        this.powerSupplyService = powerSupplyService;
        this.motherboardService = motherboardService;
    }

    protected static String getNotFoundMessage(String id) {
        return "Entity was not Found. Id of entity: " + id;
    }

    public Specs getSpecs(SpecsIds specsIds) {
        if (specsIds == null) {
            throw new IllegalArgumentException("specsIds cannot be null");
        }

        return new Specs(
                cpuService.getById(specsIds.cpuId()),
                gpuService.getById(specsIds.gpuId()),
                motherboardService.getById(specsIds.motherboardId()),
                ramService.getById(specsIds.ramId()),
                ssdService.getById(specsIds.ssdId()),
                hddService.getById(specsIds.hddId()),
                powerSupplyService.getById(specsIds.powerSupplyId()),
                pcCaseService.getById(specsIds.pcCaseId())
        );
    }

    public SpecsForEnergyConsumption getSpecsForConfigurator(SpecsIdsForEnergyConsumption specsIdsForEnergyConsumption) {
        if (specsIdsForEnergyConsumption == null) {
            throw new IllegalArgumentException("specsIdsForEnergyConsumption cannot be null");
        }

        return new SpecsForEnergyConsumption(
                cpuService.getById(specsIdsForEnergyConsumption.cpuId()),
                gpuService.getById(specsIdsForEnergyConsumption.gpuId()),
                motherboardService.getById(specsIdsForEnergyConsumption.motherboardId()),
                ramService.getById(specsIdsForEnergyConsumption.ramId()),
                ssdService.getById(specsIdsForEnergyConsumption.ssdId()),
                hddService.getById(specsIdsForEnergyConsumption.hddId())
        );
    }

    @Transactional
    @DeleteMapping
    public void deleteById(@PathVariable String id) {
        T entity = repository.findById(id).orElseThrow(() -> new NoSuchElementException(getNotFoundMessage(id)));

        repository.delete(entity);
    }

    public List<T> getAll() {
        List<T> entities = repository.findAll();

        if(entities.isEmpty()) {
            throw new PcNotFoundException("No entities found!");
        }

        return entities;
    }
}
