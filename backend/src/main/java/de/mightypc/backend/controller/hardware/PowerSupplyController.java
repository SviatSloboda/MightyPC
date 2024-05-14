package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
@RequestMapping("api/hardware/psu")
public class PowerSupplyController extends BaseController<PowerSupply, PowerSupplyService> {
    protected PowerSupplyController(PowerSupplyService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PowerSupply save(@RequestBody CreatePowerSupply createPowerSupply) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createPowerSupply.hardwareSpec().name(),
                createPowerSupply.hardwareSpec().description(),
                createPowerSupply.hardwareSpec().price(),
                createPowerSupply.hardwareSpec().rating()
        );

        return service.save(new PowerSupply(UUID.randomUUID().toString(), hardwareSpec, createPowerSupply.power(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreatePowerSupply[] createPowerSupply) {
        for (CreatePowerSupply powerSupply : createPowerSupply) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    powerSupply.hardwareSpec().name(),
                    powerSupply.hardwareSpec().description(),
                    powerSupply.hardwareSpec().price(),
                    powerSupply.hardwareSpec().rating()
            );

            service.save(new PowerSupply(UUID.randomUUID().toString(), hardwareSpec, powerSupply.power(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<PowerSupply> getSortedPowerSuppliesByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<PowerSupply> getSortedPowerSuppliesByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<PowerSupply> getFilteredPowerSuppliesByPrice(Pageable pageable,
                                                             @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                                             @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/power")
    public Page<PowerSupply> getFilteredPowerSuppliesByPower(Pageable pageable,
                                                             @RequestParam(value = "lowest", defaultValue = "0") int minimalPower,
                                                             @RequestParam(value = "highest", defaultValue = "999999") int maximalPower) {
        return service.getAllWithFilteringByPowerAsPages(pageable, minimalPower, maximalPower);
    }
}
