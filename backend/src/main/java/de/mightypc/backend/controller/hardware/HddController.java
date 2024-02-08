package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.createspecs.CreateHdd;
import de.mightypc.backend.service.hardware.HddService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/hdd")
public class HddController extends BaseController<HDD, String, HddService> {
    protected HddController(HddService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HDD save(CreateHdd createHdd){
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createHdd.name(),
                createHdd.description(),
                createHdd.price(),
                createHdd.rating()
        );

        return service.save(new HDD(hardwareSpec, createHdd.capacity(), createHdd.energyConsumption()));
    }
}

