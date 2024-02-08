package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.createspecs.CreateGpu;
import de.mightypc.backend.service.hardware.GpuService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/hardware/gpu")
public class GpuController extends BaseController<GPU, String, GpuService>{
    protected GpuController(GpuService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GPU save(CreateGpu createGpu){
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createGpu.name(),
                createGpu.description(),
                createGpu.price(),
                createGpu.rating()
        );

        return service.save(new GPU(hardwareSpec, createGpu.performance(), createGpu.energyConsumption()));
    }
}