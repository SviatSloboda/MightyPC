package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.model.pc.createpc.CreateWorkstation;
import de.mightypc.backend.model.pc.createpc.WorkstationResponse;
import de.mightypc.backend.service.pc.WorkstationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        service.deleteById(id);
    }

    @PutMapping
    public void update(@RequestBody WorkstationResponse workstationResponse) {
        service.update(workstationResponse);
    }

    @GetMapping("/sort/price")
    public Page<Workstation> getSortedWorkstationsByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<Workstation> getSortedWorkstationsByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<Workstation> getFilteredWorkstationsByPrice(Pageable pageable,
                                            @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                            @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }

    @GetMapping("/filter/energy-consumption")
    public Page<Workstation> getFilteredWorkstationsByEnergyConsumption(Pageable pageable,
                                                        @RequestParam(value = "lowest", defaultValue = "0") int lowestEnergyConsumption,
                                                        @RequestParam(value = "highest", defaultValue = "999999") int highestEnergyConsumption) {
        return service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, lowestEnergyConsumption, highestEnergyConsumption);
    }
}
