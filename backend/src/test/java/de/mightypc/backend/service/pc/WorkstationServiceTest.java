package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.WorkstationNotFoundException;
import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.model.pc.createpc.CreateWorkstation;
import de.mightypc.backend.model.pc.createpc.WorkstationResponse;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.repository.pc.WorkstationRepository;
import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.RamService;
import de.mightypc.backend.service.hardware.SsdService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;

class WorkstationServiceTest {
    private final WorkstationRepository workstationRepository = mock(WorkstationRepository.class);
    private final CpuRepository cpuRepository = mock(CpuRepository.class);
    private final GpuRepository gpuRepository = mock(GpuRepository.class);
    private final SsdRepository ssdRepository = mock(SsdRepository.class);
    private final HddRepository hddRepository = mock(HddRepository.class);
    private final RamRepository ramRepository = mock(RamRepository.class);
    private final PcCaseRepository pcCaseRepository = mock(PcCaseRepository.class);
    private final PowerSupplyRepository powerSupplyRepository = mock(PowerSupplyRepository.class);
    private final MotherboardRepository motherboardRepository = mock(MotherboardRepository.class);

    private final CpuService cpuService = new CpuService(cpuRepository);
    private final GpuService gpuService = new GpuService(gpuRepository);
    private final SsdService ssdService = new SsdService(ssdRepository);
    private final HddService hddService = new HddService(hddRepository);
    private final RamService ramService = new RamService(ramRepository);
    private final PcCaseService pcCaseService = new PcCaseService(pcCaseRepository);
    private final PowerSupplyService powerSupplyService = new PowerSupplyService(powerSupplyRepository);
    private final MotherboardService motherboardService = new MotherboardService(motherboardRepository);


    private final WorkstationService service = new WorkstationService(
            workstationRepository,
            cpuService,
            gpuService,
            ssdService,
            hddService,
            ramService,
            pcCaseService,
            powerSupplyService,
            motherboardService
    );

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(10), 2.5f), 5);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(10), 2.5f), "DDR", 5, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testPcCase", "test", new BigDecimal(10), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(10), 2.5f), 5);

    private final Specs specs = new Specs(cpu, gpu, motherboard, ram, ssd, hdd, powerSupply, pcCase);

    private final SpecsIds specsIds = new SpecsIds(
            "cpuId",
            "gpuId",
            "motherboardId",
            "ramId",
            "ssdId",
            "hddId",
            "powerSupplyId",
            "pcCaseId"
    );

    private final Workstation testWorkstation = new Workstation(
            "testId",
            new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(666), 2.33f),
            new Specs(
                    cpu,
                    gpu,
                    motherboard,
                    ram,
                    ssd,
                    hdd,
                    powerSupply,
                    pcCase
            ),
            1,
            1,
            900,
            Collections.emptyList()
    );

    private final Workstation testWorkstation2 = new Workstation(
            "testId",
            new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(333), 4.53f),
            new Specs(
                    cpu.withId("cpuId2"),
                    gpu.withId("gpuId2"),
                    motherboard.withId("motherboardId2"),
                    ram.withId("ramId2"),
                    ssd.withId("ssdId2"),
                    hdd.withId("hddId2"),
                    powerSupply.withId("powerSupplyId2"),
                    pcCase.withId("pcCaseId")
            ),
            1800,
            2,
            2,
            Collections.emptyList()
    );
    private final List<Workstation> workstations = new ArrayList<>(List.of(testWorkstation, testWorkstation2));

    private final WorkstationResponse workstationResponse = new WorkstationResponse(
            "testId",
            new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(666), 2.33f),
            specsIds,
            new SpecsNames(
                    "testCpu",
                    "testGpu",
                    "testMotherboard",
                    "testRam",
                    "testSsd",
                    "testHdd",
                    "testPowerSupply",
                    "testPcCase"
            ),
            1,
            1,
            900,
            Collections.emptyList()
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(List.of(testWorkstation, testWorkstation2), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllByPage_whenPagesIsEmpty_thenThrowWorkationNotFoundException(){
        // Arrange
        when(workstationRepository.findAll(pageable)).thenReturn(new PageImpl<>(new ArrayList<>()));

        // Act & Assert
        assertThrows(WorkstationNotFoundException.class,
                () -> service.getAllByPage(pageable));
    }


    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(List.of(testWorkstation2, testWorkstation), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(List.of(testWorkstation2, testWorkstation), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(List.of(testWorkstation, testWorkstation2), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(Collections.singletonList(testWorkstation), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 1000);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<Workstation> expected = new PageImpl<>(Collections.singletonList(testWorkstation), pageable, 8);
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        Page<Workstation> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getSpecs_shouldReturnProperSpecs() {
        // Arrange
        Specs expected = new Specs(cpu, gpu, motherboard, ram, ssd, hdd, powerSupply, pcCase);

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));

        // Act
        Specs actual = service.getSpecs(specsIds);

        // Assert
        verify(cpuRepository).findById("cpuId");
        verify(gpuRepository).findById("gpuId");
        verify(motherboardRepository).findById("motherboardId");
        verify(ramRepository).findById("ramId");
        verify(ssdRepository).findById("ssdId");
        verify(hddRepository).findById("hddId");
        verify(powerSupplyRepository).findById("powerSupplyId");
        verify(pcCaseRepository).findById("pcCaseId");

        assertEquals(expected, actual);
    }

    @Test
    void getSpecs_shouldThrowIllegalArgumentException_whenSpecsIdsAreNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.getSpecs(null),
                "specsIds cannot be null");
    }

    @Test
    void deleteById_shouldDeleteWorkstationProperly() {
        // Arrange
        when(workstationRepository.findById("workstationId")).thenReturn(Optional.of(testWorkstation));
        // Act
        service.deleteById("workstationId");

        // Assert
        verify(workstationRepository).findById("workstationId");
        verify(workstationRepository).delete(testWorkstation);
    }

    @Test
    void getAll_shouldReturnAllGpus() {
        // Arrange
        when(workstationRepository.findAll()).thenReturn(workstations);

        // Act
        List<Workstation> actual = service.getAll();

        // Assert
        verify(workstationRepository).findAll();
        assertEquals(workstations, actual);
    }

    @Test
    void getNotFoundMessage_shouldReturnNotFoundMessage() {
        // Arrange
        String expected = "Entity was not Found. Id of entity: 23";

        // Act
        String actual = PcBaseService.getNotFoundMessage("23");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getId_shouldReturnWorkstationIdProperly() {
        // Act
        String actual = service.getId(testWorkstation);

        // Assert
        assertEquals(testWorkstation.id(), actual);
    }

    @Test
    void getWorkstationResponseByIdOfWorkstation_shouldReturnWorkstationResponseProperly() {
        // Arrange
        when(workstationRepository.findById("testId")).thenReturn(Optional.of(testWorkstation));

        // Act
        WorkstationResponse actual = service.getWorkstationResponseByIdOfWorkstation("testId");

        // Assert
        verify(workstationRepository).findById("testId");
        assertEquals(workstationResponse, actual);
    }

    @Test
    void getWorkstationResponseByIdOfWorkstation_shouldThrowWorkstationNotFoundException_whenWorkstationNotFound() {
        // Act & Assert
        assertThrows(WorkstationNotFoundException.class,
                () -> service.getWorkstationResponseByIdOfWorkstation("notExistingId"),
                "Entity was not Found. Id of entity: notExistingId");
    }

    @Test
    void save_shouldSaveWorkstationProperly() {
        // Arrange
        HardwareSpec hardwareSpec = new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(449), 2.33f);
        Workstation expected = new Workstation(
                "testId",
                hardwareSpec,
                specs,
                1,
                1,
                100,
                Collections.emptyList()
        );

        CreateWorkstation createWorkstation = new CreateWorkstation(
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(0), 2.33f),
                specsIds,
                1,
                1
        );

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));

        when(workstationRepository.save(any(Workstation.class))).thenReturn(expected);

        // Act
        Workstation actual = service.save(createWorkstation);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void saveAll_shouldSaveMultipleWorkstationsProperly() {
        // Arrange
        List<CreateWorkstation> createWorkstations = List.of(
                new CreateWorkstation(
                        new HardwareSpec("TestWorkstation1", "Cool Workstation 1!", new BigDecimal(0), 2.33f),
                        specsIds,
                        1,
                        1
                ),
                new CreateWorkstation(
                        new HardwareSpec("TestWorkstation2", "Cool Workstation 2!", new BigDecimal(0), 4.53f),
                        specsIds,
                        2,
                        2
                )
        );

        List<Workstation> expectedWorkstations = List.of(
                new Workstation(
                        "testId1",
                        new HardwareSpec("TestWorkstation1", "Cool Workstation 1!", new BigDecimal(449), 2.33f),
                        specs,
                        1,
                        1,
                        100,
                        Collections.emptyList()
                ),
                new Workstation(
                        "testId2",
                        new HardwareSpec("TestWorkstation2", "Cool Workstation 2!", new BigDecimal(888), 4.53f),
                        specs,
                        2,
                        2,
                        200,
                        Collections.emptyList()
                )
        );

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));

        when(workstationRepository.saveAll(anyList())).thenReturn(expectedWorkstations);

        // Act
        service.saveAll(createWorkstations);

        // Assert
        verify(workstationRepository).saveAll(anyList());
    }

    @Test
    void attachPhoto_shouldAttachPhotoToWorkstation() {
        // Arrange
        String id = "testId";
        String photoUrl = "http://example.com/photo.jpg";
        List<String> photos = new ArrayList<>(List.of("http://example.com/photo1.jpg"));
        Workstation workstation = new Workstation(
                "testId",
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(666), 2.33f),
                specs,
                1,
                1,
                900,
                photos
        );

        when(workstationRepository.findById(id)).thenReturn(Optional.of(workstation));
        when(workstationRepository.save(workstation)).thenReturn(workstation);

        // Act
        service.attachPhoto(id, photoUrl);

        // Assert
        assertTrue(workstation.photos().contains(photoUrl));
    }

    @Test
    void getAllByPage_shouldReturnPageOfWorkstations() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        List<Workstation> workstationList = List.of(testWorkstation, testWorkstation2);
        Page<Workstation> expectedPage = new PageImpl<>(workstationList, pageable, workstationList.size());
        when(workstationRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<WorkstationResponse> actualPage = service.getAllByPage(pageable);

        // Assert
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(workstationList.size(), actualPage.getContent().size());
    }

    @Test
    void update_shouldUpdateWorkstation() {
        // Arrange
        Workstation existingWorkstation = new Workstation(
                "testId",
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(666), 2.33f),
                specs,
                1,
                1,
                900,
                Collections.emptyList()
        );
        Workstation updatedWorkstation = new Workstation(
                "testId",
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(888), 4.53f),
                specs,
                2,
                2,
                1200,
                Collections.emptyList()
        );
        WorkstationResponse workstationResponse = new WorkstationResponse(
                "testId",
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(888), 4.53f),
                specsIds,
                new SpecsNames(
                        "testCpu",
                        "testGpu",
                        "testMotherboard",
                        "testRam",
                        "testSsd",
                        "testHdd",
                        "testPowerSupply",
                        "testPcCase"
                ),
                2,
                2,
                1200,
                Collections.emptyList()
        );
        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));

        when(workstationRepository.findById("testId")).thenReturn(Optional.of(existingWorkstation));
        when(workstationRepository.save(any(Workstation.class))).thenReturn(updatedWorkstation);

        // Act
        service.update(workstationResponse);

        // Assert
        assertEquals(1200, updatedWorkstation.energyConsumption());
        assertEquals(new BigDecimal(888), updatedWorkstation.hardwareSpec().price());
        assertEquals(2, updatedWorkstation.cpuNumber());
    }
}
