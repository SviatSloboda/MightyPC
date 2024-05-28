package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;

import de.mightypc.backend.model.hardware.createspecs.CreateRam;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/ram")
public class RamController extends BaseController<RAM, RamService> {
    protected RamController(RamService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RAM save(@RequestBody CreateRam createRam) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createRam.hardwareSpec().name(),
                createRam.hardwareSpec().description(),
                createRam.hardwareSpec().price(),
                createRam.hardwareSpec().rating()
        );

        return service.save(new RAM(UUID.randomUUID().toString(), hardwareSpec, createRam.type(), createRam.energyConsumption(), createRam.memorySize(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateRam[] createRam) {
        for (CreateRam ram : createRam) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    ram.hardwareSpec().name(),
                    ram.hardwareSpec().description(),
                    ram.hardwareSpec().price(),
                    ram.hardwareSpec().rating()
            );

            service.save(new RAM(UUID.randomUUID().toString(), hardwareSpec, ram.type(), ram.energyConsumption(), ram.memorySize(), Collections.emptyList()));
        }
    }

    @GetMapping("/page")
    public Page<RAM> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<RAM> getRams(Pageable pageable,
                             @RequestParam(value = "sortType", required = false) String sortType,
                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                             @RequestParam(value = "minimalMemorySize", required = false) Integer minimalMemorySize,
                             @RequestParam(value = "maximalMemorySize", required = false) Integer maximalMemorySize,
                             @RequestParam(value = "type", required = false) String type) {

        return service.getRams(pageable, sortType, lowestPrice, highestPrice, minimalMemorySize, maximalMemorySize, type);
    }
}

