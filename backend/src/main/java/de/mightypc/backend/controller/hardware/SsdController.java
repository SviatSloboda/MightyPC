package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;

import de.mightypc.backend.model.hardware.createspecs.CreateSsd;
import de.mightypc.backend.service.hardware.SsdService;
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
@RequestMapping("api/hardware/ssd")
public class SsdController extends BaseController<SSD, SsdService> {
    protected SsdController(SsdService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SSD save(@RequestBody CreateSsd createSsd) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createSsd.hardwareSpec().name(),
                createSsd.hardwareSpec().description(),
                createSsd.hardwareSpec().price(),
                createSsd.hardwareSpec().rating()
        );

        return service.save(new SSD(UUID.randomUUID().toString(), hardwareSpec, createSsd.capacity(), createSsd.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateSsd[] createSsd) {
        for (CreateSsd ssd : createSsd) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    ssd.hardwareSpec().name(),
                    ssd.hardwareSpec().description(),
                    ssd.hardwareSpec().price(),
                    ssd.hardwareSpec().rating()
            );

            service.save(new SSD(UUID.randomUUID().toString(), hardwareSpec, ssd.capacity(), ssd.energyConsumption(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<SSD> getSortedSsdsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<SSD> getSortedSsdsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<SSD> getFilteredSsdsByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/capacity")
    public Page<SSD> getFilteredSsdsByCapacity(Pageable pageable,
                                               @RequestParam(value = "lowest", defaultValue = "0") int minimalCapacity,
                                               @RequestParam(value = "highest", defaultValue = "999999") int maximalCapacity) {
        return service.getAllWithFilteringByCapacityAsPages(pageable, minimalCapacity, maximalCapacity);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<SSD> getFilteredSsdsByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
