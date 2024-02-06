package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.model.specs.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/power-supply")
public class PowerSupplyController {
    private final PowerSupplyService powerSupplyService;

    public PowerSupplyController(PowerSupplyService powerSupplyService) {
        this.powerSupplyService = powerSupplyService;
    }

    @GetMapping
    public ResponseEntity<List<PowerSupply>> getAllPowerSupplys() {
        List<PowerSupply> powerSupplys = powerSupplyService.getAll();

        if (powerSupplys.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(powerSupplys, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowerSupply> getById(@PathVariable String id) {
        return powerSupplyService.getById(id)
                .map(powerSupply -> new ResponseEntity<>(powerSupply, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreatePowerSupply createPowerSupply) {
        PowerSupply powerSupply = new PowerSupply(UUID.randomUUID().toString(),
                createPowerSupply.name(),
                createPowerSupply.description(),
                createPowerSupply.power(),
                createPowerSupply.price());

        if (powerSupplyService.save(powerSupply)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody PowerSupply powerSupply) {
        if (powerSupplyService.update(powerSupply)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (powerSupplyService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
