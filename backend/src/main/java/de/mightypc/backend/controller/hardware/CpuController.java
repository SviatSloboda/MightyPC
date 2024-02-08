package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.createspecs.CreateCpu;
import de.mightypc.backend.service.hardware.CpuService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/hardware/cpu")
public class CpuController extends BaseController<CPU, String, CpuService> {
    protected CpuController(CpuService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CPU saveCpu(CreateCpu createCpu) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createCpu.name(),
                createCpu.description(),
                createCpu.price(),
                createCpu.rating()
        );

        return service.save(new CPU(hardwareSpec, createCpu.performance(), createCpu.energyConsumption()));
    }
}