package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.model.specs.createspecs.CreateRam;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/ram")
public class RamController extends BaseController<RAM, String, RamService> {
    protected RamController(RamService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RAM save(CreateRam createRam) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createRam.name(),
                createRam.description(),
                createRam.price(),
                createRam.rating()
        );

        return service.save(new RAM(hardwareSpec, createRam.type(), createRam.energyConsumption(), createRam.memorySize()));
    }
}
