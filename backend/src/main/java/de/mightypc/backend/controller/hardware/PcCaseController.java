package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.model.specs.createspecs.CreatePcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.service.hardware.PcCaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("hardware/pc-case")
public class PcCaseController {
    private final PcCaseService pcCaseService;

    public PcCaseController(PcCaseService pcCaseService){
        this.pcCaseService = pcCaseService;
    }

    @GetMapping
    public ResponseEntity<List<PcCase>> getAllPcCases() {
        List<PcCase> pcCases = pcCaseService.getAll();

        if (pcCases.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(pcCases, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PcCase> getById(@PathVariable String id) {
        return pcCaseService.getById(id)
                .map(pcCase -> new ResponseEntity<>(pcCase, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreatePcCase createPcCase) {
        PcCase pcCase = new PcCase(UUID.randomUUID().toString(),
                createPcCase.name(),
                createPcCase.price());

        if (pcCaseService.save(pcCase)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody PcCase pcCase) {
        if (pcCaseService.update(pcCase)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (pcCaseService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
