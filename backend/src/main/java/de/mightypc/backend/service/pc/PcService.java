package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.PcNotFoundException;
import de.mightypc.backend.model.configurator.SpecsForEnergyConsumption;
import de.mightypc.backend.model.configurator.SpecsIdsForEnergyConsumption;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Specs;
import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.model.hardware.SpecsNames;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.*;

import de.mightypc.backend.repository.pc.PcRepository;
import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.RamService;
import de.mightypc.backend.service.hardware.SsdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PcService extends PcBaseService<PC, PcRepository> {
    private final PcRepository pcRepository;

    @Autowired
    public PcService(PcRepository pcRepository, CpuService cpuService, GpuService gpuService, SsdService ssdService, HddService hddService, RamService ramService, PcCaseService pcCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        super(pcRepository, cpuService, gpuService, ssdService, hddService, ramService, pcCaseService, powerSupplyService, motherboardService);
        this.pcRepository = pcRepository;
    }

    protected String getId(PC entity) {
        return entity.id();
    }

    @Transactional
    public PC saveNewPc(CreatePC createPC) {
        return pcRepository.save(createPc(createPC));
    }

    @Transactional
    public void save(PC pcToSave) {
        repository.save(pcToSave);
    }

    public PC createPc(CreatePC createPC) {
        Specs specs = getSpecs(createPC.specsIds());

        HardwareSpec hardwareSpec = new HardwareSpec(createPC.hardwareSpec().name(), createPC.hardwareSpec().description(), getTotalPrice(specs), createPC.hardwareSpec().rating());

        return new PC(hardwareSpec, specs,
                calculateEnergyConsumptionOfPc(
                        new SpecsForEnergyConsumption(
                                specs.cpu(),
                                specs.gpu(),
                                specs.motherboard(),
                                specs.ram(),
                                specs.ssd(),
                                specs.hdd()
                        )
                )
        );
    }

    @Transactional
    public void saveAll(List<CreatePC> createPCS) {
        List<PC> pcsToSave = new ArrayList<>();

        for (CreatePC createPC : createPCS) {
            Specs specs = getSpecs(createPC.specsIds());

            HardwareSpec hardwareSpec = new HardwareSpec(createPC.hardwareSpec().name(), createPC.hardwareSpec().description(), getTotalPrice(specs), createPC.hardwareSpec().rating());

            PC pc = new PC(hardwareSpec, specs,
                    calculateEnergyConsumptionOfPc(
                            new SpecsForEnergyConsumption(
                                    specs.cpu(),
                                    specs.gpu(),
                                    specs.motherboard(),
                                    specs.ram(),
                                    specs.ssd(),
                                    specs.hdd()
                            )
                    ));
            pcsToSave.add(pc);
        }

        pcRepository.saveAll(pcsToSave);
    }

    public PcResponse createPcResponse(PC pc) {
        return new PcResponse(
                pc.id(),
                pc.hardwareSpec(),
                new SpecsIds(
                        pc.specs().cpu().id(),
                        pc.specs().gpu().id(),
                        pc.specs().motherboard().id(),
                        pc.specs().ram().id(),
                        pc.specs().ssd().id(),
                        pc.specs().hdd().id(),
                        pc.specs().powerSupply().id(),
                        pc.specs().pcCase().id()
                ),
                new SpecsNames(pc.specs().cpu().hardwareSpec().name(),
                        pc.specs().gpu().hardwareSpec().name(),
                        pc.specs().motherboard().hardwareSpec().name(),
                        pc.specs().ram().hardwareSpec().name(),
                        pc.specs().ssd().hardwareSpec().name(),
                        pc.specs().hdd().hardwareSpec().name(),
                        pc.specs().powerSupply().hardwareSpec().name(),
                        pc.specs().pcCase().hardwareSpec().name()
                ),
                pc.energyConsumption(),
                pc.photos());
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

        totalPrice = totalPrice.add(BigDecimal.valueOf(250));
        totalPrice = totalPrice.setScale(-2, RoundingMode.UP);

        return totalPrice.subtract(BigDecimal.ONE);
    }


    @Transactional(readOnly = true)
    public PcResponse getById(String id) {
        PC pc = pcRepository.findById(id).orElseThrow(() -> new PcNotFoundException(getNotFoundMessage(id)));

        return createPcResponse(pc);
    }

    @Transactional
    public void update(PcResponse pcResponse) {
        Specs specs = getSpecs(pcResponse.specsIds());

        HardwareSpec hardwareSpec = new HardwareSpec(
                pcResponse.hardwareSpec().name(),
                pcResponse.hardwareSpec().description(),
                getTotalPrice(specs),
                pcResponse.hardwareSpec().rating()
        );

        PC pc = new PC(pcResponse.id(), hardwareSpec, specs,
                calculateEnergyConsumptionOfPc(
                        new SpecsForEnergyConsumption(
                                specs.cpu(),
                                specs.gpu(),
                                specs.motherboard(),
                                specs.ram(),
                                specs.ssd(),
                                specs.hdd()
                        )
                ), pcResponse.photos());

        pcRepository.save(pc);
    }

    @Transactional
    public void attachPhoto(String id, String photoUrl) {
        Optional<PC> pc = pcRepository.findById(id);
        if (pc.isPresent()) {
            PC currPc = pc.get();
            List<String> photos = pc.get().photos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            pcRepository.save(currPc.withPhotos(photos));
        }
    }

    @Transactional(readOnly = true)
    public Page<PcResponse> getAllByPage(Pageable pageable) {
        Page<PC> page = pcRepository.findAll(pageable);

        if (page.isEmpty()) throw new PcNotFoundException("No Pcs found.");

        List<PcResponse> responses = page.getContent().stream().map(this::createPcResponse).toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    public int calculateEnergyConsumptionWithConvertingSpecsIdsIntoSpecs(SpecsIdsForEnergyConsumption specsIdsForEnergyConsumption) {
        SpecsForEnergyConsumption specsForEnergyConsumption = getSpecsForConfigurator(specsIdsForEnergyConsumption);

        return calculateEnergyConsumptionOfPc(specsForEnergyConsumption);
    }

    private int calculateEnergyConsumptionOfPc(SpecsForEnergyConsumption specs) {
        int totalConsumption = specs.cpu().energyConsumption() +
                               specs.gpu().energyConsumption() +
                               specs.ssd().energyConsumption() +
                               specs.hdd().energyConsumption() +
                               specs.motherboard().energyConsumption() +
                               specs.ram().energyConsumption();

        int remainder = totalConsumption % 50;
        if (remainder == 0) {
            return totalConsumption;
        }

        return totalConsumption + 50 - remainder;
    }
}
