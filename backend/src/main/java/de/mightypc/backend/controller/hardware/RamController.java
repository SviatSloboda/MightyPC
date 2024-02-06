package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.model.specs.createspecs.CreateRam;
import de.mightypc.backend.service.hardware.RamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/ram")
public class RamController {
    private final RamService ramService;

    public RamController(RamService ramService){
        this.ramService = ramService;
    }

    @GetMapping
    public ResponseEntity<List<RAM>> getAllRAMs() {
        List<RAM> rams = ramService.getAll();

        if (rams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(rams, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RAM> getById(@PathVariable String id) {
        return ramService.getById(id)
                .map(ram -> new ResponseEntity<>(ram, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateRam createRAM) {
        RAM ram = new RAM(UUID.randomUUID().toString(),
                createRAM.name(),
                createRAM.description(),
                createRAM.type(),
                createRAM.energyConsumption(),
                createRAM.memorySize(),
                createRAM.price());

        if (ramService.save(ram)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody RAM ram) {
        if (ramService.update(ram)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (ramService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
