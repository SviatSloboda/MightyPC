package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreatePcCase;
import de.mightypc.backend.service.hardware.PcCaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/pc-case")
public class PcCaseController extends BaseController<PcCase, PcCaseService> {
    protected PcCaseController(PcCaseService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PcCase save(@RequestBody CreatePcCase createPcCase) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createPcCase.hardwareSpec().name(),
                createPcCase.hardwareSpec().description(),
                createPcCase.hardwareSpec().price(),
                createPcCase.hardwareSpec().rating()
        );

        return service.save(new PcCase(UUID.randomUUID().toString(), hardwareSpec, createPcCase.dimensions(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreatePcCase[] createPcCase) {
        for (CreatePcCase pcCase : createPcCase) {

            HardwareSpec hardwareSpec = new HardwareSpec(
                    pcCase.hardwareSpec().name(),
                    pcCase.hardwareSpec().description(),
                    pcCase.hardwareSpec().price(),
                    pcCase.hardwareSpec().rating()
            );

            service.save(new PcCase(UUID.randomUUID().toString(), hardwareSpec, pcCase.dimensions(), Collections.emptyList()));
        }
    }

    @GetMapping("/sort/price")
    public Page<PcCase> getSortedPcCasesByPrice(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfPriceDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfPriceAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/sort/rating")
    public Page<PcCase> getSortedPcCasesByRating(Pageable pageable, @RequestParam(value = "type", defaultValue = "desc") String type) {
        if (type.equals("desc")) {
            return service.getAllWithSortingOfRatingDescAsPages(pageable);
        } else if (type.equals("asc")) {
            return service.getAllWithSortingOfRatingAscAsPages(pageable);
        } else {
            throw new IllegalStateException("Not matching value! Accepted only desc and asc!!!");
        }
    }

    @GetMapping("/filter/price")
    public Page<PcCase> getFilteredPcCasesByPrice(Pageable pageable,
                                                  @RequestParam(value = "lowest", defaultValue = "0") int lowestPrice,
                                                  @RequestParam(value = "highest", defaultValue = "999999") int highestPrice
    ) {
        return service.getAllWithFilteringByPriceAsPages(pageable, lowestPrice, highestPrice);
    }
}
