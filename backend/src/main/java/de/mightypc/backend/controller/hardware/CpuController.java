package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateCpu;
import de.mightypc.backend.service.hardware.CpuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/cpu")
public class CpuController extends BaseController<CPU, CpuService> {
    private final CpuService cpuService;

    protected CpuController(CpuService service, CpuService cpuService) {
        super(service);
        this.cpuService = cpuService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CPU saveCpu(@RequestBody CreateCpu createCpu) {
        HardwareSpec hardwareSpec = new HardwareSpec(createCpu.hardwareSpec().name(), createCpu.hardwareSpec().description(), createCpu.hardwareSpec().price(), createCpu.hardwareSpec().rating());

        return service.save(new CPU(UUID.randomUUID().toString(), hardwareSpec, createCpu.energyConsumption(), createCpu.socket(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAllCpus(@RequestBody CreateCpu[] createCpu) {
        for (CreateCpu cpu : createCpu) {
            HardwareSpec hardwareSpec = new HardwareSpec(cpu.hardwareSpec().name(), cpu.hardwareSpec().description(), cpu.hardwareSpec().price(), cpu.hardwareSpec().rating());

            service.save(new CPU(UUID.randomUUID().toString(), hardwareSpec, cpu.energyConsumption(), cpu.socket(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<CPU> getSortedCpusByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<CPU> getSortedCpusByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<CPU> getFilteredCpusByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/socket")
    public Page<CPU> getFilteredCpusBySocket(Pageable pageable, @RequestParam(value = "socket", defaultValue = "AM4") String socket) {
        return service.getAllWithFilteringBySocketAsPages(pageable, socket);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<CPU> getFilteredCpusByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }

    @GetMapping("/socket/{cpuId}")
    public String getSocketOfCpu(@PathVariable String cpuId) {
        return cpuService.getSocketOfCpuById(cpuId);
    }
}
