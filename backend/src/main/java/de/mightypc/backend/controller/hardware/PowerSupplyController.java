package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.model.specs.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/hardware/psu")
public class PowerSupplyController extends BaseController<PowerSupply, String, PowerSupplyService> {
    protected PowerSupplyController(PowerSupplyService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PowerSupply save(@RequestBody CreatePowerSupply createPowerSupply) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createPowerSupply.hardwareSpec().name(),
                createPowerSupply.hardwareSpec().description(),
                createPowerSupply.hardwareSpec().price(),
                createPowerSupply.hardwareSpec().rating()
        );

        return service.save(new PowerSupply(hardwareSpec, createPowerSupply.power()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreatePowerSupply[] createPowerSupply) {
        for (CreatePowerSupply powerSupply : createPowerSupply) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    UUID.randomUUID().toString(),
                    powerSupply.hardwareSpec().name(),
                    powerSupply.hardwareSpec().description(),
                    powerSupply.hardwareSpec().price(),
                    powerSupply.hardwareSpec().rating()
            );

            service.save(new PowerSupply(hardwareSpec, powerSupply.power()));
        }
    }
}
