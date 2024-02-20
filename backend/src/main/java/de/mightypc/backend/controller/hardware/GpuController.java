package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.createspecs.CreateGpu;
import de.mightypc.backend.service.hardware.GpuService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/gpu")
public class GpuController extends BaseController<GPU, GpuService> {
    protected GpuController(GpuService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GPU save(@RequestBody CreateGpu createGpu) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createGpu.hardwareSpec().name(),
                createGpu.hardwareSpec().description(),
                createGpu.hardwareSpec().price(),
                createGpu.hardwareSpec().rating()
        );

        return service.save(new GPU(UUID.randomUUID().toString(), hardwareSpec, createGpu.performance(), createGpu.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateGpu[] createGpu) {
        for (CreateGpu gpu : createGpu) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    gpu.hardwareSpec().name(),
                    gpu.hardwareSpec().description(),
                    gpu.hardwareSpec().price(),
                    gpu.hardwareSpec().rating()
            );

            service.save(new GPU(UUID.randomUUID().toString(), hardwareSpec, gpu.performance(), gpu.energyConsumption(), Collections.emptyList()));
        }
    }
}