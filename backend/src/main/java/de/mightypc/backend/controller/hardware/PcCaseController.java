package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.model.specs.createspecs.CreatePcCase;
import de.mightypc.backend.service.hardware.PcCaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("hardware/pc-case")
public class PcCaseController extends BaseController<PcCase, String, PcCaseService> {
    protected PcCaseController(PcCaseService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PcCase save(CreatePcCase createPcCase) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createPcCase.name(),
                createPcCase.description(),
                createPcCase.price(),
                createPcCase.rating()
        );

        return service.save(new PcCase(hardwareSpec, createPcCase.dimensions()));
    }
}