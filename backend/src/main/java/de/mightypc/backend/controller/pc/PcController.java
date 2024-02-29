package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.service.pc.PcService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pc")
public class PcController extends BaseController<PC, PcService> {
    protected PcController(PcService service) {
        super(service);
    }

    @PostMapping
    public PC savePc(@RequestBody CreatePC createPC) {
        return service.save(createPC);
    }

    @PostMapping("/all")
    void saveAll(@RequestBody List<CreatePC> createPCS) {
        service.saveAll(createPCS);
    }
}
