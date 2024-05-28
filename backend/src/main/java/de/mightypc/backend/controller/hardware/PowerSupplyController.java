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

    @GetMapping("/page")
    public Page<PowerSupply> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<PowerSupply> getPowerSupplies(Pageable pageable,
                                              @RequestParam(value = "sortType", required = false) String sortType,
                                              @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                                              @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                                              @RequestParam(value = "minimalPower", required = false) Integer minimalPower,
                                              @RequestParam(value = "maximalPower", required = false) Integer maximalPower) {

        return service.getPowerSupplies(pageable, sortType, lowestPrice, highestPrice, minimalPower, maximalPower);
    }
}

