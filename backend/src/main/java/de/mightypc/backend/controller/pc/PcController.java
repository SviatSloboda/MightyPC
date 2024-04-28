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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/sort/price")
    public Page<PC> getSortedPcsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<PC> getSortedPcsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<PC> getFilteredPcsByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<PC> getFilteredPcsByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
