package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.model.specs.createspecs.CreateMotherboard;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/motherboard")
public class MotherboardController {
    private final MotherboardService motherboardService;

    public MotherboardController(MotherboardService motherboardService){
        this.motherboardService = motherboardService;
    }

    @GetMapping
    public ResponseEntity<List<Motherboard>> getAllMotherboards() {
        List<Motherboard> motherboards = motherboardService.getAll();

        if (motherboards.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(motherboards, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Motherboard> getById(@PathVariable String id) {
        return motherboardService.getById(id)
                .map(motherboard -> new ResponseEntity<>(motherboard, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateMotherboard createMotherboard) {
        Motherboard motherboard = new Motherboard(UUID.randomUUID().toString(),
                createMotherboard.name(),
                createMotherboard.description(),
                createMotherboard.price(),
                createMotherboard.energyConsumption(),
                createMotherboard.graphicCardCompatibility(),
                createMotherboard.processorCompatibility());

        if (motherboardService.save(motherboard)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody Motherboard motherboard) {
        if (motherboardService.update(motherboard)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (motherboardService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
