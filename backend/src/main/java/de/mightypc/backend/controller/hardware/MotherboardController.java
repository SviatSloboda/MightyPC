package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.model.specs.createspecs.CreateMotherboard;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("api/hardware/motherboard")
public class MotherboardController extends BaseController<Motherboard, String, MotherboardService> {
    protected MotherboardController(MotherboardService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard save(@RequestBody CreateMotherboard createMotherboard) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createMotherboard.hardwareSpec().name(),
                createMotherboard.hardwareSpec().description(),
                createMotherboard.hardwareSpec().price(),
                createMotherboard.hardwareSpec().rating()
        );

        return service.save(new Motherboard(
                hardwareSpec,
                createMotherboard.energyConsumption(),
                createMotherboard.graphicCardCompatibility(),
                createMotherboard.processorCompatibility()
        ));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateMotherboard[] createMotherboard) {
        for (CreateMotherboard motherboard : createMotherboard) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    UUID.randomUUID().toString(),
                    motherboard.hardwareSpec().name(),
                    motherboard.hardwareSpec().description(),
                    motherboard.hardwareSpec().price(),
                    motherboard.hardwareSpec().rating()
            );

            service.save(new Motherboard(
                    hardwareSpec,
                    motherboard.energyConsumption(),
                    motherboard.graphicCardCompatibility(),
                    motherboard.processorCompatibility()
            ));
        }
    }
}
