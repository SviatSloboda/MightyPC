package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;

import de.mightypc.backend.model.hardware.createspecs.CreateRam;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/ram")
public class RamController extends BaseController<RAM, RamService> {
    protected RamController(RamService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RAM save(@RequestBody CreateRam createRam) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createRam.hardwareSpec().name(),
                createRam.hardwareSpec().description(),
                createRam.hardwareSpec().price(),
                createRam.hardwareSpec().rating()
        );

        return service.save(new RAM(UUID.randomUUID().toString(), hardwareSpec, createRam.type(), createRam.energyConsumption(), createRam.memorySize(), Collections.emptyList()));
    }

    @PostMapping("all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateRam[] createRam) {
        for (CreateRam ram : createRam) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    ram.hardwareSpec().name(),
                    ram.hardwareSpec().description(),
                    ram.hardwareSpec().price(),
                    ram.hardwareSpec().rating()
            );

            service.save(new RAM(UUID.randomUUID().toString(), hardwareSpec, ram.type(), ram.energyConsumption(), ram.memorySize(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<RAM> getSortedRamsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<RAM> getSortedRamsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<RAM> getFilteredRamsByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/memory-size")
    public Page<RAM> getFilteredRamsByCapacity(Pageable pageable,
                                               @RequestParam(value = "lowest", defaultValue = "0") int minimalMemorySize,
                                               @RequestParam(value = "highest", defaultValue = "999999") int maximalMemorySize) {
        return service.getAllWithFilteringByMemorySizeAsPages(pageable, minimalMemorySize, maximalMemorySize);
    }

    @GetMapping("/filter/type")
    public Page<RAM> getFilteredRamsByType(Pageable pageable, @RequestParam(value = "type", defaultValue = "DDR4") String type) {
        return service.getAllWithFilteringByTypeAsPages(pageable, type);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<RAM> getFilteredRamsByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
