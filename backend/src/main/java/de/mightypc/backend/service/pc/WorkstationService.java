package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.WorkstationNotFoundException;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Specs;
import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.model.hardware.SpecsNames;
import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.model.pc.createpc.CreateWorkstation;
import de.mightypc.backend.model.pc.createpc.WorkstationResponse;
import de.mightypc.backend.repository.pc.WorkstationRepository;
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
import java.util.Comparator;
import java.util.List;

@Service
public class WorkstationService extends PcBaseService<Workstation, WorkstationRepository> {
    private final WorkstationRepository workstationRepository;

    @Autowired
    public WorkstationService(WorkstationRepository workstationRepository, CpuService cpuService, GpuService gpuService, SsdService workstationService, HddService hddService, RamService ramService, PcCaseService workstationCaseService, PowerSupplyService powerSupplyService, MotherboardService motherboardService) {
        super(workstationRepository, cpuService, gpuService, workstationService, hddService, ramService, workstationCaseService, powerSupplyService, motherboardService);
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
                        calculateEnergyConsumptionOfWorkstation(createWorkstation.cpuNumber(), createWorkstation.gpuNumber(), specs)
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
                    calculateEnergyConsumptionOfWorkstation(createWorkstation.cpuNumber(), createWorkstation.gpuNumber(), specs)
            );
            workStationsToSave.add(workstation);
        }

        workstationRepository.saveAll(workStationsToSave);
    }

    private WorkstationResponse createWorkstationResponse(Workstation workstation) {
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
    public Workstation attachPhoto(String id, String photoUrl) {
        Workstation workstation = getById(id);
        List<String> photos = workstation.photos();

        if (photos == null) {
            photos = new ArrayList<>();
        }

        photos.addFirst(photoUrl);

        return workstationRepository.save(workstation.withPhotos(photos));
    }

    private Workstation getById(String id) {
        return workstationRepository.findById(id).orElseThrow(() -> new WorkstationNotFoundException("There is no such workstation with id: " + id));
    }

    @Transactional(readOnly = true)
    public WorkstationResponse getWorkstationResponseByIdOfWorkstation(String id) {
        Workstation workstation = workstationRepository.findById(id).orElseThrow(() -> new WorkstationNotFoundException(getNotFoundMessage(id)));

        return createWorkstationResponse(workstation);
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

    private int calculateEnergyConsumptionOfWorkstation(int cpuNumber, int gpuNumber, Specs specs) {
        int totalConsumption = (specs.cpu().energyConsumption() * cpuNumber) +
                               (specs.gpu().energyConsumption() * gpuNumber) +
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

    @Transactional(readOnly = true)
    public Page<WorkstationResponse> getAllByPage(Pageable pageable) {
        Page<Workstation> page = workstationRepository.findAll(pageable);

        if (page.isEmpty()) throw new WorkstationNotFoundException("No Workstations found.");

        List<WorkstationResponse> responses = page.getContent().stream()
                .map(this::createWorkstationResponse)
                .toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }


    @Transactional(readOnly = true)
    public Page<Workstation> getWorkstations(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer lowestEnergyConsumption, Integer highestEnergyConsumption) {
        List<Workstation> workstations = getAll();

        if (lowestPrice != null && highestPrice != null) {
            workstations = workstations.stream()
                    .filter(workstation -> workstation.hardwareSpec().price().intValue() >= lowestPrice &&
                                           workstation.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (lowestEnergyConsumption != null && highestEnergyConsumption != null) {
            workstations = workstations.stream()
                    .filter(workstation -> workstation.energyConsumption() >= lowestEnergyConsumption &&
                                           workstation.energyConsumption() <= highestEnergyConsumption)
                    .toList();
        }

        if (sortType != null) {

            switch (sortType) {
                case "price-asc":
                    workstations = workstations.stream()
                            .sorted(Comparator.comparing(workstation -> workstation.hardwareSpec().price()))
                            .toList();
                    break;
                case "price-desc":
                    workstations = workstations.stream()
                            .sorted(Comparator.comparing((Workstation workstation) -> workstation.hardwareSpec().price()).reversed())
                            .toList();
                    break;
                case "rating-asc":
                    workstations = workstations.stream()
                            .sorted(Comparator.comparing(workstation -> workstation.hardwareSpec().rating()))
                            .toList();
                    break;
                case "rating-desc":
                    workstations = workstations.stream()
                            .sorted(Comparator.comparing((Workstation workstation) -> workstation.hardwareSpec().rating()).reversed())
                            .toList();
                    break;
                default:
                    break;
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), workstations.size());
        return new PageImpl<>(workstations.subList(start, end), pageable, workstations.size());
    }
}
