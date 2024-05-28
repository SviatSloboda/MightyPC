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

    @GetMapping("/page")
    public Page<HDD> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<HDD> getHdds(Pageable pageable,
                             @RequestParam(value = "sortType", required = false) String sortType,
                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                             @RequestParam(value = "minimalCapacity", required = false) Integer minimalCapacity,
                             @RequestParam(value = "maximalCapacity", required = false) Integer maximalCapacity,
                             @RequestParam(value = "lowestEnergyConsumption", required = false) Integer lowestEnergyConsumption,
                             @RequestParam(value = "highestEnergyConsumption", required = false) Integer highestEnergyConsumption) {
        return service.getHdds(pageable, sortType, lowestPrice, highestPrice, minimalCapacity, maximalCapacity, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
