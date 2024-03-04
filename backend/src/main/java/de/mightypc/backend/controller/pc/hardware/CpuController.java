package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.model.pc.specs.CPU;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.createspecs.CreateCpu;
import de.mightypc.backend.service.pc.hardware.CpuService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/cpu")
public class CpuController extends BaseController<CPU, CpuService> {
    protected CpuController(CpuService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CPU saveCpu(@RequestBody CreateCpu createCpu) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createCpu.hardwareSpec().name(),
                createCpu.hardwareSpec().description(),
                createCpu.hardwareSpec().price(),
                createCpu.hardwareSpec().rating()
        );

        return service.save(new CPU(UUID.randomUUID().toString(), hardwareSpec, createCpu.performance(), createCpu.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAllCpus(@RequestBody CreateCpu[] createCpu) {
        for (CreateCpu cpu : createCpu) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    cpu.hardwareSpec().name(),
                    cpu.hardwareSpec().description(),
                    cpu.hardwareSpec().price(),
                    cpu.hardwareSpec().rating()
            );

            service.save(new CPU(UUID.randomUUID().toString(), hardwareSpec, cpu.performance(), cpu.energyConsumption(), Collections.emptyList()));
        }
    }
}