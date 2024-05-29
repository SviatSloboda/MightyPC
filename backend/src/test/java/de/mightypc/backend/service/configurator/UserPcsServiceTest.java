package de.mightypc.backend.service.configurator;

import de.mightypc.backend.exception.pc.UserPcNotFoundException;
import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.repository.pc.PcRepository;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.RamService;
import de.mightypc.backend.service.hardware.SsdService;
import de.mightypc.backend.service.pc.PcService;
import de.mightypc.backend.service.shop.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class UserPcsServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PcRepository pcRepository = mock(PcRepository.class);
    private final CpuRepository cpuRepository = mock(CpuRepository.class);
    private final GpuRepository gpuRepository = mock(GpuRepository.class);
    private final SsdRepository ssdRepository = mock(SsdRepository.class);
    private final HddRepository hddRepository = mock(HddRepository.class);
    private final RamRepository ramRepository = mock(RamRepository.class);
    private final PcCaseRepository pcCaseRepository = mock(PcCaseRepository.class);
    private final PowerSupplyRepository powerSupplyRepository = mock(PowerSupplyRepository.class);
    private final MotherboardRepository motherboardRepository = mock(MotherboardRepository.class);

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService = new UserService(userRepository, passwordEncoder);
    private final CpuService cpuService = new CpuService(cpuRepository);
    private final GpuService gpuService = new GpuService(gpuRepository);
    private final SsdService ssdService = new SsdService(ssdRepository);
    private final HddService hddService = new HddService(hddRepository);
    private final RamService ramService = new RamService(ramRepository);
    private final PcCaseService pcCaseService = new PcCaseService(pcCaseRepository);
    private final PowerSupplyService powerSupplyService = new PowerSupplyService(powerSupplyRepository);
    private final MotherboardService motherboardService = new MotherboardService(motherboardRepository);

    private final PcService pcService = new PcService(
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

    private final UserPcsService userPcsService = new UserPcsService(
            userService,
            pcService,
            userRepository
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
            "testPc1",
            new HardwareSpec("TestPc", "Cool Pc!", new BigDecimal(666), 2.33f),
            specs,
            900,
            Collections.emptyList()
    );

    private final PC testPc2 = new PC(
            "testPc2",
            new HardwareSpec("TestPc", "Cool Pc!", new BigDecimal(333), 4.53f),
            specs,
            1800,
            Collections.emptyList()
    );

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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "testId",
                "testEmail",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(List.of(testPc, testPc2)),
                true,
                "user",
                "23.32",
                "link"
        );
    }

    @Test
    void savePc() {
        // Arrange
        CreatePC createPC = new CreatePC(
                new HardwareSpec("testing", "test", new BigDecimal(333), 2.34f),
                new SpecsIds(
                        "cpuId",
                        "gpuId",
                        "motherboardId",
                        "ramId",
                        "ssdId",
                        "hddId",
                        "powerSupplyId",
                        "pcCaseId"
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

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userPcsService.savePc("testId", createPC);

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(3, user.getPcs().size());
    }

    @Test
    void getPcByUserIdAndPcIdAsPcResponse_whenThereIsNoSuchPc_thenThrowUserPcNotFoundException() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));
        user.setPcs(new ArrayList<>());

        // Act & Assert
        assertThrows(UserPcNotFoundException.class, () -> userPcsService.getPcByUserIdAndPcIdAsPcResponse("testId", "roflId"));
    }

    @Test
    void addPrivateUserPcToAllPcs() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userPcsService.addPrivateUserPcToAllPcs("testId", "testPc2");

        // Assert
        verify(userRepository).findById("testId");
        verify(pcRepository).save(testPc2);
    }

    @Test
    void deletePc() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userPcsService.deletePc("testId", "testPc2");

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(1, user.getPcs().size());
    }

    @Test
    void deleteAll() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userPcsService.deleteAll("testId");

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(0, user.getPcs().size());
    }

    @Test
    void getAllPcsAsListOfPcResponseByUserId() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 8);

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        Page<PcResponse> actual = userPcsService.getAllPcsAsListOfPcResponseByUserId("testId", pageable);

        // Assert
        assertEquals(2, actual.getContent().size());
    }

    @Test
    void getPcById() {
        // Arrange
        PcResponse expected = new PcResponse(
                "testPc1",
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
                ),
                900,
                Collections.emptyList()
        );

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        PcResponse actual = userPcsService.getPcByUserIdAndPcIdAsPcResponse("testId", "testPc1");

        // Assert
        verify(userRepository).findById("testId");
        assertEquals(expected, actual);
    }

    @Test
    void update_shouldUpdatePC() {
        // Arrange
        PC updatedPC = new PC(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(888), 4.53f),
                specs,
                1200,
                Collections.emptyList()
        );
        PcResponse pcResponse = new PcResponse(
                "testPc1",
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
                ),
                900,
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

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userPcsService.update("testId", pcResponse);

        // Assert
        verify(cpuRepository).findById("cpuId");
        verify(gpuRepository).findById("gpuId");
        verify(motherboardRepository).findById("motherboardId");
        verify(ramRepository).findById("ramId");
        verify(ssdRepository).findById("ssdId");
        verify(hddRepository).findById("hddId");
        verify(powerSupplyRepository).findById("powerSupplyId");
        verify(pcCaseRepository).findById("pcCaseId");
        verify(userRepository).findById("testId");
        verify(userRepository, times(2)).save(user);

        assertEquals(1200, updatedPC.energyConsumption());
        assertEquals(new BigDecimal(888), updatedPC.hardwareSpec().price());
    }
}
