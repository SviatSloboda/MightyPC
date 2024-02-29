package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.pc.specs.GPU;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.createspecs.CreateGpu;
import de.mightypc.backend.service.pc.hardware.GpuService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

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