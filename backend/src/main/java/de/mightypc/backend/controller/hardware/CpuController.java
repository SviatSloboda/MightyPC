package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateCpu;
import de.mightypc.backend.service.hardware.CpuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

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
        HardwareSpec hardwareSpec = new HardwareSpec(
                createCpu.hardwareSpec().name(),
                createCpu.hardwareSpec().description(),
                createCpu.hardwareSpec().price(),
                createCpu.hardwareSpec().rating()
        );

        return service.save(
                new CPU(UUID.randomUUID().toString(),
                        hardwareSpec,
                        createCpu.energyConsumption(),
                        createCpu.socket(),
                        Collections.emptyList()
                )
        );
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAllCpus(@RequestBody CreateCpu[] createCpu) {
        for (CreateCpu cpu : createCpu) {
            HardwareSpec hardwareSpec = new HardwareSpec(cpu.hardwareSpec().name(),
                    cpu.hardwareSpec().description(),
                    cpu.hardwareSpec().price(),
                    cpu.hardwareSpec().rating()
            );

            service.save(
                    new CPU(UUID.randomUUID().toString(),
                            hardwareSpec, cpu.energyConsumption(),
                            cpu.socket(),
                            Collections.emptyList()
                    )
            );
        }
    }

    @GetMapping("/page")
    public Page<CPU> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<CPU> getCpus(Pageable pageable,
                             @RequestParam(value = "sortType", required = false) String sortType,
                             @RequestParam(value = "socket", required = false) String socket,
                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice) {

        return cpuService.getCpus(pageable, sortType, socket, lowestPrice, highestPrice);
    }

    @GetMapping("/socket/{cpuId}")
    public String getSocketOfCpu(@PathVariable String cpuId) {
        return cpuService.getSocketOfCpuById(cpuId);
    }
}
