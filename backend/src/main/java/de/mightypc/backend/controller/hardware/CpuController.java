package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.createspecs.CreateCpu;
import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.service.hardware.CpuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/cpu")
public class CpuController {
    private final CpuService cpuService;

    public CpuController(CpuService cpuService) {
        this.cpuService = cpuService;
    }

    @GetMapping
    public List<CPU> getAllCPUs() {
        List<CPU> cpus = cpuService.getAll();

        return cpus;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CPU> getById(@PathVariable String id) {
        return cpuService.getById(id)
                .map(cpu -> new ResponseEntity<>(cpu, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateCpu createCpu) {
        CPU cpu = new CPU(UUID.randomUUID().toString(),
                createCpu.name(),
                createCpu.description(),
                createCpu.price(),
                createCpu.performance(),
                createCpu.energyConsumption(),
                createCpu.rating());

        if (cpuService.save(cpu)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping("/all")
    public ResponseEntity<Void> saveAll(@RequestBody CreateCpu[] cpuArr) {
        for(CreateCpu createCpu: cpuArr){
            CPU cpu = new CPU(UUID.randomUUID().toString(),
                    createCpu.name(),
                    createCpu.description(),
                    createCpu.price(),
                    createCpu.performance(),
                    createCpu.energyConsumption(),
                    createCpu.rating());

            cpuService.save(cpu);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

@PutMapping
public ResponseEntity<Void> update(@RequestBody CPU cpu) {
    if (cpuService.update(cpu)) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) {
    if (cpuService.deleteById(id)) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}
}