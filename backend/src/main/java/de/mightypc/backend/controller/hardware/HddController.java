package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.createspecs.CreateHdd;
import de.mightypc.backend.service.hardware.HddService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("api/hardware/hdd")
public class HddController extends BaseController<HDD, String, HddService> {
    protected HddController(HddService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HDD save(@RequestBody CreateHdd createHdd) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                UUID.randomUUID().toString(),
                createHdd.hardwareSpec().name(),
                createHdd.hardwareSpec().description(),
                createHdd.hardwareSpec().price(),
                createHdd.hardwareSpec().rating()
        );

        return service.save(new HDD(hardwareSpec, createHdd.capacity(), createHdd.energyConsumption()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateHdd[] createHdd) {
        for (CreateHdd hdd : createHdd) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    UUID.randomUUID().toString(),
                    hdd.hardwareSpec().name(),
                    hdd.hardwareSpec().description(),
                    hdd.hardwareSpec().price(),
                    hdd.hardwareSpec().rating()
            );

            service.save(new HDD(hardwareSpec, hdd.capacity(), hdd.energyConsumption()));
        }
    }
}

