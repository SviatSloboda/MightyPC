package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateGpu;
import de.mightypc.backend.service.hardware.GpuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

        return service.save(new GPU(UUID.randomUUID().toString(), hardwareSpec, createGpu.energyConsumption(), Collections.emptyList()));
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

            service.save(new GPU(UUID.randomUUID().toString(), hardwareSpec, gpu.energyConsumption(), Collections.emptyList()));
        }
    }

    @GetMapping("/page")
    public Page<GPU> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<GPU> getGpus(Pageable pageable,
                             @RequestParam(value = "sortType", required = false) String sortType,
                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                             @RequestParam(value = "lowestEnergyConsumption", required = false) Integer lowestEnergyConsumption,
                             @RequestParam(value = "highestEnergyConsumption", required = false) Integer highestEnergyConsumption) {

        return service.getGpus(pageable, sortType, lowestPrice, highestPrice, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
