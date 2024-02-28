package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.model.pc.specs.HDD;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.createspecs.CreateHdd;
import de.mightypc.backend.service.pc.hardware.HddService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("api/hardware/hdd")
public class HddController extends BaseController<HDD, HddService> {
    protected HddController(HddService service) {
        super(service);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HDD save(@RequestBody CreateHdd createHdd) {
        HardwareSpec hardwareSpec = new HardwareSpec(
                createHdd.hardwareSpec().name(),
                createHdd.hardwareSpec().description(),
                createHdd.hardwareSpec().price(),
                createHdd.hardwareSpec().rating()
        );

        return service.save(new HDD(UUID.randomUUID().toString(), hardwareSpec, createHdd.capacity(), createHdd.energyConsumption(), Collections.emptyList()));
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAll(@RequestBody CreateHdd[] createHdd) {
        for (CreateHdd hdd : createHdd) {
            HardwareSpec hardwareSpec = new HardwareSpec(
                    hdd.hardwareSpec().name(),
                    hdd.hardwareSpec().description(),
                    hdd.hardwareSpec().price(),
                    hdd.hardwareSpec().rating()
            );

            service.save(new HDD(UUID.randomUUID().toString(), hardwareSpec, hdd.capacity(), hdd.energyConsumption(), Collections.emptyList()));
        }
    }
}

