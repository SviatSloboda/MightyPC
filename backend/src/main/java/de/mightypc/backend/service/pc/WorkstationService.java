package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.model.pc.createpc.CreateWorkstation;
import de.mightypc.backend.model.pc.createpc.WorkstationResponse;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.Specs;
import de.mightypc.backend.model.pc.specs.SpecsIds;
import de.mightypc.backend.model.pc.specs.SpecsNames;
import de.mightypc.backend.repository.pc.WorkstationRepository;
import de.mightypc.backend.service.pc.hardware.CpuService;
import de.mightypc.backend.service.pc.hardware.GpuService;
import de.mightypc.backend.service.pc.hardware.HddService;
import de.mightypc.backend.service.pc.hardware.MotherboardService;
import de.mightypc.backend.service.pc.hardware.PcCaseService;
import de.mightypc.backend.service.pc.hardware.PowerSupplyService;
import de.mightypc.backend.service.pc.hardware.RamService;
import de.mightypc.backend.service.pc.hardware.SsdService;
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
public class WorkstationService extends PcBaseService<Workstation, WorkstationRepository> {
    private final WorkstationRepository workstationRepository;

    @Autowired
    public WorkstationService(WorkstationRepository workstationRepository, CpuService cpuService, GpuService gpuService, SsdService workstationService, HddService hddService, RamService ramService, PcCaseService pcCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        super(workstationRepository, cpuService, gpuService, workstationService, hddService, ramService, pcCaseService, powerSupplyService, motherboardService);
        this.workstationRepository = workstationRepository;
    }


    protected String getId(Workstation entity) {
        return entity.id();
    }

    @Transactional
    public Workstation save(CreateWorkstation createWorkstation) {
        Specs specs = getSpecs(createWorkstation.specsIds());

        HardwareSpec hardwareSpec = new HardwareSpec(
                createWorkstation.hardwareSpec().name(),
                createWorkstation.hardwareSpec().description(),
                getTotalPrice(specs, createWorkstation.cpuNumber(), createWorkstation.gpuNumber()),
                createWorkstation.hardwareSpec().rating()
        );

        return workstationRepository.save(
                new Workstation(
                        hardwareSpec,
                        specs,
                        createWorkstation.cpuNumber(),
                        createWorkstation.gpuNumber(),
                        calculateEnergyConsumptionOfPC(createWorkstation.cpuNumber(), createWorkstation.gpuNumber(), specs)
                ));
    }

    @Transactional
    public void saveAll(List<CreateWorkstation> createWorkstations) {
        List<Workstation> workStationsToSave = new ArrayList<>();

        for (CreateWorkstation createWorkstation : createWorkstations) {
            Specs specs = getSpecs(createWorkstation.specsIds());

            HardwareSpec hardwareSpec = new HardwareSpec(
                    createWorkstation.hardwareSpec().name(),
                    createWorkstation.hardwareSpec().description(),
                    getTotalPrice(specs, createWorkstation.cpuNumber(), createWorkstation.gpuNumber()),
                    createWorkstation.hardwareSpec().rating()
            );

            Workstation workstation = new Workstation(
                    hardwareSpec,
                    specs,
                    createWorkstation.cpuNumber(),
                    createWorkstation.gpuNumber(),
                    calculateEnergyConsumptionOfPC(createWorkstation.cpuNumber(), createWorkstation.gpuNumber(), specs)
            );
            workStationsToSave.add(workstation);
        }

        workstationRepository.saveAll(workStationsToSave);
    }

    private WorkstationResponse createPcResponse(Workstation workstation) {
        return new WorkstationResponse(
                workstation.id(),
                workstation.hardwareSpec(),
                new SpecsIds(
                        workstation.specs().cpu().id(),
                        workstation.specs().gpu().id(),
                        workstation.specs().motherboard().id(),
                        workstation.specs().ram().id(),
                        workstation.specs().ssd().id(),
                        workstation.specs().hdd().id(),
                        workstation.specs().powerSupply().id(),
                        workstation.specs().pcCase().id()
                ),
                new SpecsNames(
                        workstation.specs().cpu().hardwareSpec().name(),
                        workstation.specs().gpu().hardwareSpec().name(),
                        workstation.specs().motherboard().hardwareSpec().name(),
                        workstation.specs().ram().hardwareSpec().name(),
                        workstation.specs().ssd().hardwareSpec().name(),
                        workstation.specs().hdd().hardwareSpec().name(),
                        workstation.specs().powerSupply().hardwareSpec().name(),
                        workstation.specs().pcCase().hardwareSpec().name()
                ),
                workstation.cpuNumber(),
                workstation.gpuNumber(),
                workstation.energyConsumption(),
                workstation.photos()
        );
    }

    private BigDecimal getTotalPrice(Specs specs, int cpuNumber, int gpuNumber) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        totalPrice = totalPrice.add((specs.cpu().hardwareSpec().price()).multiply(BigDecimal.valueOf(cpuNumber)));
        totalPrice = totalPrice.add((specs.gpu().hardwareSpec().price()).multiply(BigDecimal.valueOf(gpuNumber)));
        totalPrice = totalPrice.add(specs.hdd().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.ssd().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.ram().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.motherboard().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.powerSupply().hardwareSpec().price());
        totalPrice = totalPrice.add(specs.pcCase().hardwareSpec().price());

        totalPrice = totalPrice.add(BigDecimal.valueOf(350));
        totalPrice = totalPrice.setScale(-2, RoundingMode.UP);

        return totalPrice.subtract(BigDecimal.ONE);
    }

    @Transactional
    public void attachPhoto(String id, String photoUrl) {
        Optional<Workstation> workstation = workstationRepository.findById(id);
        if (workstation.isPresent()) {
            Workstation currWorkstation = workstation.get();
            List<String> photos = workstation.get().photos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            workstationRepository.save(currWorkstation.withPhotos(photos));
        }
    }

    @Transactional(readOnly = true)
    public WorkstationResponse getById(String id) {
        Workstation workstation = workstationRepository.findById(id).orElseThrow(() -> new HardwareNotFoundException(getNotFoundMessage(id)));

        return createPcResponse(workstation);
    }

    @Transactional
    public void update(WorkstationResponse workstationResponse) {
        Specs specs = getSpecs(workstationResponse.specsIds());

        HardwareSpec hardwareSpec = new HardwareSpec(
                workstationResponse.hardwareSpec().name(),
                workstationResponse.hardwareSpec().description(),
                getTotalPrice(specs, workstationResponse.cpuNumber(), workstationResponse.gpuNumber()),
                workstationResponse.hardwareSpec().rating()
        );

        Workstation workstation = new Workstation(
                workstationResponse.id(),
                hardwareSpec,
                specs,
                workstationResponse.cpuNumber(),
                workstationResponse.gpuNumber(),
                workstationResponse.energyConsumption(),
                workstationResponse.photos()
        );

        workstationRepository.save(workstation);
    }

    public int calculateEnergyConsumptionOfPC(int cpuNumber, int gpuNumber, Specs specs) {
        int totalConsumption = (specs.cpu().energyConsumption() * cpuNumber) +
                               (specs.gpu().energyConsumption() * gpuNumber) +
                               specs.ssd().energyConsumption() +
                               specs.hdd().energyConsumption() +
                               specs.motherboard().energyConsumption() +
                               specs.ram().energyConsumption() +
                               specs.gpu().energyConsumption();

        int remainder = totalConsumption % 50;
        if (remainder == 0) {
            return totalConsumption;
        }

        return totalConsumption + 50 - remainder;
    }

    @Transactional(readOnly = true)
    public Page<WorkstationResponse> getAllByPage(Pageable pageable) {
        Page<Workstation> page = workstationRepository.findAll(pageable);

        if (page.isEmpty()) throw new HardwareNotFoundException("No Workstations found.");

        List<WorkstationResponse> responses = page.getContent().stream()
                .map(this::createPcResponse)
                .toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }
}