package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.model.specs.createspecs.CreateRam;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("api/hardware/ram")
public class RamController extends BaseController<RAM, String, RamService> {
    protected RamController(RamService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RAM save(@RequestBody CreateRam createRam) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createRam.hardwareSpec().name(),
                createRam.hardwareSpec().description(),
                createRam.hardwareSpec().price(),
                createRam.hardwareSpec().rating()
        );

        return service.save(new RAM(hardwareSpec, createRam.type(), createRam.energyConsumption(), createRam.memorySize()));
    }

    @PostMapping("all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateRam[] createRam) {
        for (CreateRam ram : createRam) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    UUID.randomUUID().toString(),
                    ram.hardwareSpec().name(),
                    ram.hardwareSpec().description(),
                    ram.hardwareSpec().price(),
                    ram.hardwareSpec().rating()
            );

            service.save(new RAM(hardwareSpec, ram.type(), ram.energyConsumption(), ram.memorySize()));
        }
    }
}
