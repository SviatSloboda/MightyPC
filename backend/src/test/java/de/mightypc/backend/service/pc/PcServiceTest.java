package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.PcNotFoundException;
import de.mightypc.backend.model.configurator.SpecsForEnergyConsumption;
import de.mightypc.backend.model.configurator.SpecsIdsForEnergyConsumption;
import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.repository.pc.PcRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;

class PcServiceTest {
    private final PcRepository pcRepository = mock(PcRepository.class);
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

    private final PcService service = new PcService(
            pcRepository,
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

    private final PC testPc = new PC(
            "testId",
            new HardwareSpec("TestPc", "Cool Pc!", new BigDecimal(666), 2.33f),
            specs,
            900,
            Collections.emptyList()
    );

    private final PC testPc2 = new PC(
            "testId",
            new HardwareSpec("TestPc", "Cool Pc!", new BigDecimal(333), 4.53f),
            specs,
            1800,
            Collections.emptyList()
    );

    private final List<PC> pcs = new ArrayList<>(List.of(testPc, testPc2));

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

    private final PcResponse pcResponse = new PcResponse(
            "testId",
            new HardwareSpec("TestPc", "Cool Pc!", new BigDecimal(666), 2.33f),
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
            ), 900, Collections.emptyList());
    private final PageRequest pageable = PageRequest.of(0, 8);

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<PC> expected = new PageImpl<>(List.of(testPc, testPc2), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<PC> expected = new PageImpl<>(List.of(testPc2, testPc), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<PC> expected = new PageImpl<>(List.of(testPc2, testPc), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<PC> expected = new PageImpl<>(List.of(testPc, testPc2), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<PC> expected = new PageImpl<>(Collections.singletonList(testPc), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 1000);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<PC> expected = new PageImpl<>(Collections.singletonList(testPc), pageable, 8);
        when(pcRepository.findAll()).thenReturn(pcs);

        // Act
        Page<PC> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(pcRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getSpecsForConfigurator_shouldReturnProperSpecsForEnergyConsumption() {
        // Arrange
        SpecsForEnergyConsumption expected = new SpecsForEnergyConsumption(cpu, gpu, motherboard, ram, ssd, hdd);

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));

        // Act
        SpecsForEnergyConsumption actual = service.getSpecsForConfigurator(new SpecsIdsForEnergyConsumption(
                "cpuId",
                "gpuId",
                "motherboardId",
                "ramId",
                "ssdId",
                "hddId"
        ));

        // Assert
        verify(cpuRepository).findById("cpuId");
        verify(gpuRepository).findById("gpuId");
        verify(motherboardRepository).findById("motherboardId");
        verify(ramRepository).findById("ramId");
        verify(ssdRepository).findById("ssdId");
        verify(hddRepository).findById("hddId");

        assertEquals(expected, actual);
    }

    @Test
    void getSpecsForConfigurator_shouldThrowIllegalArgumentException_whenSpecsIdsForEnergyConsumptionIsNull() {
        // Act && Assert
        assertThrows(IllegalArgumentException.class,
                () -> service.getSpecsForConfigurator(null));

        verifyNoInteractions(pcRepository);
    }

    @Test
    void getId_shouldReturnPcIdProperly() {
        // Act
        String actual = service.getId(testPc);

        // Assert
        assertEquals(testPc.id(), actual);
    }

    @Test
    void getPcResponseByIdOfPc_shouldReturnPcResponseProperly() {
        // Arrange
        when(pcRepository.findById("testId")).thenReturn(Optional.of(testPc));

        // Act
        PcResponse actual = service.getPcResponseByIdOfPc("testId");

        // Assert
        verify(pcRepository).findById("testId");
        assertEquals(pcResponse, actual);
    }

    @Test
    void getPcResponseByIdOfPc_shouldThrowPcNotFoundException_whenPcNotFound() {
        // Act & Assert
        assertThrows(PcNotFoundException.class,
                () -> service.getPcResponseByIdOfPc("notExistingId"),
                "Entity was not Found. Id of entity: notExistingId");
    }

    @Test
    void calculateTotalPrice_shouldReturnCorrectPrice() {
        // Arrange
        BigDecimal expectedPrice = new BigDecimal("399");

        // Act
        BigDecimal result = service.getTotalPrice(specs);

        // Assert
        assertEquals(expectedPrice, result);
    }

    @Test
    void createPcResponse_shouldCreateResponseProperly() {
        // Arrange
        PC pc = new PC("pcId", new HardwareSpec("PC", "Description", new BigDecimal("1200.00"), 4.5f), specs, 450, Collections.emptyList());

        // Act
        PcResponse response = service.createPcResponse(pc);

        // Assert
        assertEquals("PC", response.hardwareSpec().name());
        assertEquals(450, response.energyConsumption());
    }

    @Test
    void saveNewPc_shouldSavePcProperly() {
        // Arrange
        CreatePC createPC = new CreatePC(
                new HardwareSpec("New PC", "High performance PC", new BigDecimal("1200"), 4.5f),
                specsIds
        );

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));

        when(pcRepository.save(any(PC.class))).thenReturn(testPc);

        // Act
        PC actual = service.saveNewPc(createPC);

        // Assert
        verify(pcRepository).save(any(PC.class));
        assertEquals(testPc, actual);
    }

    @Test
    void save_shouldSavePcProperly() {
        // Arrange
        when(pcRepository.save(any(PC.class))).thenReturn(testPc);

        // Act
        service.save(testPc);

        // Assert
        verify(pcRepository).save(testPc);
    }

    @Test
    void saveAll_shouldSaveMultiplePcsProperly() {
        // Arrange
        List<CreatePC> createPCs = List.of(
                new CreatePC(
                        new HardwareSpec("PC1", "Description1", new BigDecimal("1000"), 3.5f),
                        specsIds
                ),
                new CreatePC(
                        new HardwareSpec("PC2", "Description2", new BigDecimal("2000"), 4.5f),
                        specsIds
                )
        );

        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));
        when(pcCaseRepository.findById("pcCaseId")).thenReturn(Optional.of(pcCase));
        when(powerSupplyRepository.findById("powerSupplyId")).thenReturn(Optional.of(powerSupply));

        when(pcRepository.saveAll(anyList())).thenReturn(pcs);

        // Act
        service.saveAll(createPCs);

        // Assert
        verify(pcRepository).saveAll(anyList());
    }

    @Test
    void update_shouldUpdatePC() {
        // Arrange
        PC existingPC = new PC(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(666), 2.33f),
                specs,
                900,
                Collections.emptyList()
        );
        PC updatedPC = new PC(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(888), 4.53f),
                specs,
                1200,
                Collections.emptyList()
        );
        PcResponse pcResponse = new PcResponse(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(888), 4.53f),
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

        when(pcRepository.findById("testId")).thenReturn(Optional.of(existingPC));
        when(pcRepository.save(any(PC.class))).thenReturn(updatedPC);

        // Act
        service.update(pcResponse);

        // Assert
        assertEquals(1200, updatedPC.energyConsumption());
        assertEquals(new BigDecimal(888), updatedPC.hardwareSpec().price());
    }

    @Test
    void attachPhoto_shouldAttachPhotoToPC() {
        // Arrange
        String id = "testId";
        String photoUrl = "http://example.com/photo.jpg";
        List<String> photos = new ArrayList<>(List.of("http://example.com/photo1.jpg"));
        PC pc = new PC(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(666), 2.33f),
                specs,
                900,
                photos
        );

        when(pcRepository.findById(id)).thenReturn(Optional.of(pc));
        when(pcRepository.save(pc)).thenReturn(pc);

        // Act
        service.attachPhoto(id, photoUrl);

        // Assert
        assertTrue(pc.photos().contains(photoUrl));
    }

    @Test
    void getAllByPage_shouldReturnPageOfPcs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        List<PC> pcList = List.of(testPc, testPc2);
        Page<PC> expectedPage = new PageImpl<>(pcList, pageable, pcList.size());
        when(pcRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<PcResponse> actualPage = service.getAllByPage(pageable);

        // Assert
        assertEquals(2, actualPage.getTotalElements());
        assertEquals(pcList.size(), actualPage.getContent().size());
    }

    @Test
    void deleteById_shouldDeletePcProperly() {
        // Arrange
        when(pcRepository.findById("pcId")).thenReturn(Optional.of(testPc));

        // Act
        service.deleteById("pcId");

        // Assert
        verify(pcRepository).findById("pcId");
        verify(pcRepository).delete(testPc);
    }

    @Test
    void calculateEnergyConsumptionWithConvertingSpecsIdsIntoSpecs_shouldCalculateEnergyConsumptionProperly() {
        // Arrange
        when(cpuRepository.findById("cpuId")).thenReturn(Optional.of(cpu));
        when(gpuRepository.findById("gpuId")).thenReturn(Optional.of(gpu));
        when(motherboardRepository.findById("motherboardId")).thenReturn(Optional.of(motherboard));
        when(ramRepository.findById("ramId")).thenReturn(Optional.of(ram));
        when(ssdRepository.findById("ssdId")).thenReturn(Optional.of(ssd));
        when(hddRepository.findById("hddId")).thenReturn(Optional.of(hdd));

        // Act
        int actual = service.calculateEnergyConsumptionWithConvertingSpecsIdsIntoSpecs(new SpecsIdsForEnergyConsumption(
                "cpuId",
                "gpuId",
                "motherboardId",
                "ramId",
                "ssdId",
                "hddId"
        ));

        // Assert
        assertEquals(50, actual);
    }
}
