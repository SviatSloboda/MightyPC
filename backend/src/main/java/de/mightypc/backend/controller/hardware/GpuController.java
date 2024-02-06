package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.model.specs.createspecs.CreateCpu;
import de.mightypc.backend.model.specs.createspecs.CreateGpu;
import de.mightypc.backend.service.hardware.GpuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/gpu")
public class GpuController {
    private final GpuService gpuService;

    public GpuController(GpuService gpuService){
        this.gpuService = gpuService;
    }

    @GetMapping
    public ResponseEntity<List<GPU>> getAllGPUs() {
        List<GPU> gpus = gpuService.getAll();

        if (gpus.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(gpus, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GPU> getById(@PathVariable String id) {
        return gpuService.getById(id)
                .map(gpu -> new ResponseEntity<>(gpu, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateGpu createGpu) {
        GPU gpu = new GPU(UUID.randomUUID().toString(),
                createGpu.name(),
                createGpu.description(),
                createGpu.price(),
                createGpu.performance(),
                createGpu.energyConsumption(),
                createGpu.rating());

        if (gpuService.save(gpu)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping("/all")
    public ResponseEntity<Void> saveAll(@RequestBody CreateGpu[] gpuArr){
        for(CreateGpu createGpu: gpuArr){
            GPU gpu = new GPU(UUID.randomUUID().toString(),
                    createGpu.name(),
                    createGpu.description(),
                    createGpu.price(),
                    createGpu.performance(),
                    createGpu.energyConsumption(),
                    createGpu.rating());

            gpuService.save(gpu);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody GPU gpu) {
        if (gpuService.update(gpu)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (gpuService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
