package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateHdd;
import de.mightypc.backend.service.hardware.HddService;
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
@RequestMapping("api/hardware/hdd")
public class HddController extends BaseController<HDD, HddService> {
    protected HddController(HddService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HDD save(@RequestBody CreateHdd createHdd) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createHdd.hardwareSpec().name(),
                createHdd.hardwareSpec().description(),
                createHdd.hardwareSpec().price(),
                createHdd.hardwareSpec().rating()
        );

        return service.save(new HDD(UUID.randomUUID().toString(), hardwareSpec, createHdd.capacity(), createHdd.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateHdd[] createHdd) {
        for (CreateHdd hdd : createHdd) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    hdd.hardwareSpec().name(),
                    hdd.hardwareSpec().description(),
                    hdd.hardwareSpec().price(),
                    hdd.hardwareSpec().rating()
            );

            service.save(new HDD(UUID.randomUUID().toString(), hardwareSpec, hdd.capacity(), hdd.energyConsumption(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<HDD> getSortedHddsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<HDD> getSortedHddsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<HDD> getFilteredHddsByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/capacity")
    public Page<HDD> getFilteredHddsByCapacity(Pageable pageable,
                                               @RequestParam(value = "lowest", defaultValue = "0") int minimalCapacity,
                                               @RequestParam(value = "highest", defaultValue = "999999") int maximalCapacity) {
        return service.getAllWithFilteringByCapacityAsPages(pageable, minimalCapacity, maximalCapacity);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<HDD> getFilteredHddsByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
