package de.mightypc.backend.service.pc;

import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.CreateSpecs;
import de.mightypc.backend.model.pc.createpc.Specs;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.repository.pc.PcRepository;
import de.mightypc.backend.service.pc.hardware.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PcService extends BaseService<PC, PcRepository> {
    private final CpuService cpuService;
    private final GpuService gpuService;
    private final SsdService ssdService;
    private final HddService hddService;
    private final RamService ramService;
    private final PcCaseService pcCaseService;
    private final PowerSupplyService powerSupplyService;
    private final MotherboardService motherboardService;

    protected PcService(PcRepository repository, CpuService cpuService, GpuService gpuService, SsdService ssdService, HddService hddService, RamService ramService, PcCaseService pcCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        super(repository);
        this.cpuService = cpuService;
        this.gpuService = gpuService;
        this.ssdService = ssdService;
        this.hddService = hddService;
        this.ramService = ramService;
        this.pcCaseService = pcCaseService;
        this.powerSupplyService = powerSupplyService;
        this.motherboardService = motherboardService;
    }

    @Override
    protected String getId(PC entity) {
        return entity.id();
    }

    @Transactional
    public PC save(CreatePC createPC) {
        Specs specs = getSpecs(createPC.createSpecs());

        HardwareSpec hardwareSpec = new HardwareSpec(
                createPC.hardwareSpec().name(),
                createPC.hardwareSpec().description(),
                getTotalPrice(specs),
                createPC.hardwareSpec().rating()
        );

        return repository.save(new PC(hardwareSpec, specs));
    }

    private Specs getSpecs(CreateSpecs createSpecs) {
        return new Specs(
                cpuService.getById(createSpecs.cpuId()),
                gpuService.getById(createSpecs.gpuId()),
                motherboardService.getById(createSpecs.motherboardId()),
                ramService.getById(createSpecs.ramId()),
                ssdService.getById(createSpecs.ssdId()),
                hddService.getById(createSpecs.hddId()),
                powerSupplyService.getById(createSpecs.powerSupplyId()),
                pcCaseService.getById(createSpecs.pcCaseId())
        );
    }

    public void saveAll(List<CreatePC> createPCS) {
        for (CreatePC createPC : createPCS) {
            Specs specs = getSpecs((createPC.createSpecs()));

            HardwareSpec hardwareSpec = new HardwareSpec(
                    createPC.hardwareSpec().name(),
                    createPC.hardwareSpec().description(),
                    getTotalPrice(specs),
                    createPC.hardwareSpec().rating()
            );

            repository.save(new PC(hardwareSpec, specs));
        }
    }

    private BigDecimal getTotalPrice(Specs specs) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        totalPrice = totalPrice.add(specs.cpu().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.gpu().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.hdd().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.ssd().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.ram().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.motherboard().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.powerSupply().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.pcCase().hardwareSpec().price());

        return totalPrice;
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<PC> ssd = repository.findById(id);
        if (ssd.isPresent()) {
            PC currPc = ssd.get();
            List<String> photos = ssd.get().photos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currPc.withPhotos(photos));
        }
    }
}

