package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.PowerSupply;
import de.mightypc.backend.model.pc.specs.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.pc.hardware.PowerSupplyService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
