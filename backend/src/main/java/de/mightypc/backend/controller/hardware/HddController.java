package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.model.specs.createspecs.CreateHdd;
import de.mightypc.backend.service.hardware.HddService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/hdd")
public class HddController {
    private final HddService hddService;

    public HddController(HddService hddService){
        this.hddService = hddService;
    }

    @GetMapping
    public ResponseEntity<List<HDD>> getAllHDDs() {
        List<HDD> hdds = hddService.getAll();

        if (hdds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(hdds, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HDD> getById(@PathVariable String id) {
        return hddService.getById(id)
                .map(hdd -> new ResponseEntity<>(hdd, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateHdd createHdd) {
        HDD hdd = new HDD(UUID.randomUUID().toString(),
                createHdd.name(),
                createHdd.description(),
                createHdd.capacity(),
                createHdd.energyConsumption(),
                createHdd.price());

        if (hddService.save(hdd)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody HDD hdd) {
        if (hddService.update(hdd)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (hddService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
