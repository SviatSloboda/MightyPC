package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.model.specs.createspecs.CreatePcCase;
import de.mightypc.backend.service.hardware.PcCaseService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("api/hardware/pc-case")
public class PcCaseController extends BaseController<PcCase, String, PcCaseService> {
    protected PcCaseController(PcCaseService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PcCase save(@RequestBody CreatePcCase createPcCase) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createPcCase.hardwareSpec().name(),
                createPcCase.hardwareSpec().description(),
                createPcCase.hardwareSpec().price(),
                createPcCase.hardwareSpec().rating()
        );

        return service.save(new PcCase(hardwareSpec, createPcCase.dimensions()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreatePcCase[] createPcCase) {
        for (CreatePcCase pcCase : createPcCase) {

            HardwareSpec hardwareSpec = new HardwareSpec(
                    UUID.randomUUID().toString(),
                    pcCase.hardwareSpec().name(),
                    pcCase.hardwareSpec().description(),
                    pcCase.hardwareSpec().price(),
                    pcCase.hardwareSpec().rating()
            );

            service.save(new PcCase(hardwareSpec, pcCase.dimensions()));
        }
    }
}