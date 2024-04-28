package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateMotherboard;
import de.mightypc.backend.service.hardware.MotherboardService;
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
@RequestMapping("api/hardware/motherboard")
public class MotherboardController extends BaseController<Motherboard, MotherboardService> {
    protected MotherboardController(MotherboardService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard save(@RequestBody CreateMotherboard createMotherboard) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createMotherboard.hardwareSpec().name(),
                createMotherboard.hardwareSpec().description(),
                createMotherboard.hardwareSpec().price(),
                createMotherboard.hardwareSpec().rating()
        );

        return service.save(new Motherboard(
                UUID.randomUUID().toString(),
                hardwareSpec,
                createMotherboard.energyConsumption(),
                createMotherboard.socket(),
                Collections.emptyList()
        ));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateMotherboard[] createMotherboard) {
        for (CreateMotherboard motherboard : createMotherboard) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    motherboard.hardwareSpec().name(),
                    motherboard.hardwareSpec().description(),
                    motherboard.hardwareSpec().price(),
                    motherboard.hardwareSpec().rating()
            );

            service.save(new Motherboard(
                    UUID.randomUUID().toString(),
                    hardwareSpec,
                    motherboard.energyConsumption(),
                    motherboard.socket(),
                    Collections.emptyList()
            ));
        }
    }


    @GetMapping("/sort/price")
    public Page<Motherboard> getSortedMotherboardsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<Motherboard> getSortedMotherboardsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<Motherboard> getFilteredMotherboardsByPrice(Pageable pageable,
                                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/socket")
    public Page<Motherboard> getFilteredMotherboardsBySocket(Pageable pageable, @RequestParam(value = "socket", defaultValue = "AM4") String socket) {
        return service.getAllWithFilteringBySocketAsPages(pageable, socket);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<Motherboard> getFilteredMotherboardsByEnergyConsumption(Pageable pageable,
                                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
