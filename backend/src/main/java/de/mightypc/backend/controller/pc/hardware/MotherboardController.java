package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.Motherboard;
import de.mightypc.backend.model.pc.specs.createspecs.CreateMotherboard;
import de.mightypc.backend.service.pc.hardware.MotherboardService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/motherboard")
public class MotherboardController extends BaseController<Motherboard, MotherboardService> {
    private final MotherboardService motherboardService;

    protected MotherboardController(MotherboardService service, MotherboardService motherboardService) {
        super(service);
        this.motherboardService = motherboardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard save(@RequestBody CreateMotherboard createMotherboard) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createMotherboard.hardwareSpec().name(),
                createMotherboard.hardwareSpec().description(),
                createMotherboard.hardwareSpec().price(),
                createMotherboard.hardwareSpec().rating()
        );

        return service.save(new Motherboard(
                UUID.randomUUID().toString(),
                hardwareSpec,
                createMotherboard.energyConsumption(),
                createMotherboard.socket(),
                Collections.emptyList()
        ));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateMotherboard[] createMotherboard) {
        for (CreateMotherboard motherboard : createMotherboard) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    motherboard.hardwareSpec().name(),
                    motherboard.hardwareSpec().description(),
                    motherboard.hardwareSpec().price(),
                    motherboard.hardwareSpec().rating()
            );

            service.save(new Motherboard(
                    UUID.randomUUID().toString(),
                    hardwareSpec,
                    motherboard.energyConsumption(),
                    motherboard.socket(),
                    Collections.emptyList()
            ));
        }
    }
}
