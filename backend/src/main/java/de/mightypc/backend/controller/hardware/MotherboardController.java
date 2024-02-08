package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.model.specs.createspecs.CreateMotherboard;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/motherboard")
public class MotherboardController extends BaseController<Motherboard, String, MotherboardService>{
    protected MotherboardController(MotherboardService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard save(CreateMotherboard createMotherboard){
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createMotherboard.name(),
                createMotherboard.description(),
                createMotherboard.price(),
                createMotherboard.rating()
        );

        return service.save(new Motherboard(
                hardwareSpec,
                createMotherboard.energyConsumption(),
                createMotherboard.graphicCardCompatibility(),
                createMotherboard.processorCompatibility()
                ));
    }
}
