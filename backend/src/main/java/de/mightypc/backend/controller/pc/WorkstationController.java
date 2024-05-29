package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.model.pc.createpc.CreateWorkstation;
import de.mightypc.backend.model.pc.createpc.WorkstationResponse;
import de.mightypc.backend.service.pc.WorkstationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/workstation")
public class WorkstationController {
    private final WorkstationService service;

    public WorkstationController(WorkstationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Workstation save(@RequestBody CreateWorkstation createWorkstation) {
        return service.save(createWorkstation);
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody List<CreateWorkstation> createWorkstationS) {
        service.saveAll(createWorkstationS);
    }

    @GetMapping("/page")
    public Page<WorkstationResponse> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/{id}")
    public WorkstationResponse getById(@PathVariable String id) {
        return service.getWorkstationResponseByIdOfWorkstation(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        service.deleteById(id);
    }

    @PutMapping
    public void update(@RequestBody WorkstationResponse workstationResponse) {
        service.update(workstationResponse);
    }

    @GetMapping("/filtered")
    public Page<Workstation> getWorkstations(Pageable pageable,
                                             @RequestParam(value = "sortType", required = false) String sortType,
                                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                                             @RequestParam(value = "lowestEnergyConsumption", required = false) Integer lowestEnergyConsumption,
                                             @RequestParam(value = "highestEnergyConsumption", required = false) Integer highestEnergyConsumption) {

        return service.getWorkstations(pageable, sortType, lowestPrice, highestPrice, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
