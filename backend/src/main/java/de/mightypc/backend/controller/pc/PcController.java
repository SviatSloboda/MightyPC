package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.configurator.SpecsIdsForEnergyConsumption;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.pc.PcService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pc")
public class PcController {
    private final PcService service;
    private final PowerSupplyService powerSupplyService;

    public PcController(PcService service, PowerSupplyService powerSupplyService) {
        this.service = service;
        this.powerSupplyService = powerSupplyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PC save(@RequestBody CreatePC createPC) {
        return service.saveNewPc(createPC);
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody List<CreatePC> createPCS) {
        service.saveAll(createPCS);
    }

    @GetMapping("/page")
    public Page<PcResponse> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/{id}")
    public PcResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        service.deleteById(id);
    }

    @PutMapping
    public void update(@RequestBody PcResponse pcResponse) {
        service.update(pcResponse);
    }

    @PostMapping("/configuration/calculate-energy-consumption")
    public Map<String, String> getAllPowerSuppliesIdsAndNamesWithPricesByEnergyConsumption(@RequestBody SpecsIdsForEnergyConsumption specsIdsForEnergyConsumption) {
        int energyConsumption = service.calculateEnergyConsumptionWithConvertingSpecsIdsIntoSpecs(specsIdsForEnergyConsumption);

        return powerSupplyService.getAllPowerSuppliesByEnergyConsumption(energyConsumption);
    }
}
