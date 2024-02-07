package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.model.specs.createspecs.CreateSsd;
import de.mightypc.backend.service.hardware.SsdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/ssd")
public class SsdController {
    private final SsdService ssdService;

    public SsdController(SsdService ssdService){
        this.ssdService = ssdService;
    }

    @GetMapping
    public ResponseEntity<List<SSD>> getAllSSDs() {
        List<SSD> ssds = ssdService.getAll();

        if (ssds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(ssds, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SSD> getById(@PathVariable String id) {
        return ssdService.getById(id)
                .map(ssd -> new ResponseEntity<>(ssd, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateSsd createSSD) {
        SSD ssd = new SSD(UUID.randomUUID().toString(),
                createSSD.name(),
                createSSD.description(),
                createSSD.energyConsumption(),
                createSSD.price(),
                createSSD.rating());

        if (ssdService.save(ssd)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SSD ssd) {
        if (ssdService.update(ssd)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (ssdService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
