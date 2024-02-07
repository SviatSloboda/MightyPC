package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.model.specs.createspecs.CreatePowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
@RequestMapping("hardware/power-supply")
public class PowerSupplyController {
    private final PowerSupplyService powerSupplyService;

    public PowerSupplyController(PowerSupplyService powerSupplyService) {
        this.powerSupplyService = powerSupplyService;
    }

    @GetMapping
    public ResponseEntity<List<PowerSupply>> getAllPowerSupplies() {
        List<PowerSupply> powerSupplies = powerSupplyService.getAll();

        if (powerSupplies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(powerSupplies, HttpStatus.OK);
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
                createPowerSupply.price(),
                createPowerSupply.rating());

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
