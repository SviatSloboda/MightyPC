package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.SSD;
import de.mightypc.backend.model.pc.specs.createspecs.CreateSsd;
import de.mightypc.backend.service.pc.hardware.SsdService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/ssd")
public class SsdController extends BaseController<SSD, SsdService> {
    protected SsdController(SsdService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SSD save(@RequestBody CreateSsd createSsd) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createSsd.hardwareSpec().name(),
                createSsd.hardwareSpec().description(),
                createSsd.hardwareSpec().price(),
                createSsd.hardwareSpec().rating()
        );

        return service.save(new SSD(UUID.randomUUID().toString(), hardwareSpec, createSsd.capacity(), createSsd.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateSsd[] createSsd) {
        for (CreateSsd ssd : createSsd) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    ssd.hardwareSpec().name(),
                    ssd.hardwareSpec().description(),
                    ssd.hardwareSpec().price(),
                    ssd.hardwareSpec().rating()
            );

            service.save(new SSD(UUID.randomUUID().toString(), hardwareSpec, ssd.capacity(), ssd.energyConsumption(), Collections.emptyList()));
        }
    }
}
