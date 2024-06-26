package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.createspecs.CreateMotherboard;
import de.mightypc.backend.service.hardware.MotherboardService;
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
@RequestMapping("api/hardware/motherboard")
public class MotherboardController extends BaseController<Motherboard, MotherboardService> {
    protected MotherboardController(MotherboardService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard save(@RequestBody CreateMotherboard createMotherboard) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createMotherboard.hardwareSpec().name(),
                createMotherboard.hardwareSpec().description(),
                createMotherboard.hardwareSpec().price(),
                createMotherboard.hardwareSpec().rating()
        );
        return service.save(new Motherboard(
                UUID.randomUUID().toString(),
                hardwareSpec,
                createMotherboard.energyConsumption(),
                createMotherboard.socket(),
                Collections.emptyList()
        ));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateMotherboard[] createMotherboard) {
        for (CreateMotherboard motherboard : createMotherboard) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    motherboard.hardwareSpec().name(),
                    motherboard.hardwareSpec().description(),
                    motherboard.hardwareSpec().price(),
                    motherboard.hardwareSpec().rating()
            );
            service.save(new Motherboard(
                    UUID.randomUUID().toString(),
                    hardwareSpec,
                    motherboard.energyConsumption(),
                    motherboard.socket(),
                    Collections.emptyList()
            ));
        }
    }

    @GetMapping("/page")
    public Page<Motherboard> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @GetMapping("/filtered")
    public Page<Motherboard> getMotherboards(Pageable pageable,
                                             @RequestParam(value = "sortType", required = false) String sortType,
                                             @RequestParam(value = "lowestPrice", required = false) Integer lowestPrice,
                                             @RequestParam(value = "highestPrice", required = false) Integer highestPrice,
                                             @RequestParam(value = "socket", required = false) String socket,
                                             @RequestParam(value = "lowestEnergyConsumption", required = false) Integer lowestEnergyConsumption,
                                             @RequestParam(value = "highestEnergyConsumption", required = false) Integer highestEnergyConsumption) {

        return service.getMotherboards(pageable, sortType, lowestPrice, highestPrice, socket, lowestEnergyConsumption, highestEnergyConsumption);
    }
}

