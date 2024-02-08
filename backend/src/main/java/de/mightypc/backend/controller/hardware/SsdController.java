package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.model.specs.createspecs.CreateSsd;
import de.mightypc.backend.service.hardware.SsdService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/ssd")
public class SsdController extends BaseController<SSD, String, SsdService> {
    protected SsdController(SsdService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SSD save(CreateSsd createSsd){
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createSsd.name(),
                createSsd.description(),
                createSsd.price(),
                createSsd.rating()
        );

        return service.save(new SSD(hardwareSpec, createSsd.energyConsumption()));
    }
}
