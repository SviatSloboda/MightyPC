package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.model.specs.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/power-supply")
public class PowerSupplyController extends BaseController<PowerSupply, String, PowerSupplyService>{
    protected PowerSupplyController(PowerSupplyService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PowerSupply save(CreatePowerSupply createPowerSupply){
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createPowerSupply.name(),
                createPowerSupply.description(),
                createPowerSupply.price(),
                createPowerSupply.rating()
        );

        return service.save(new PowerSupply(hardwareSpec, createPowerSupply.power()));
    }
}
