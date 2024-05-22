package de.mightypc.backend.service.configurator;

import de.mightypc.backend.service.hardware.CpuService;
import de.mightypc.backend.service.hardware.GpuService;
import de.mightypc.backend.service.hardware.HddService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PcCaseService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
import de.mightypc.backend.service.hardware.RamService;
import de.mightypc.backend.service.hardware.SsdService;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfiguratorServiceTest {
    private final CpuService cpuService = mock(CpuService.class);
    private final GpuService gpuService = mock(GpuService.class);
    private final MotherboardService motherboardService = mock(MotherboardService.class);
    private final SsdService ssdService = mock(SsdService.class);
    private final HddService hddService = mock(HddService.class);
    private final RamService ramService = mock(RamService.class);
    private final PcCaseService pcCaseService = mock(PcCaseService.class);
    private final PowerSupplyService powerSupplyService = mock(PowerSupplyService.class);

    private final ConfiguratorService configuratorService = new ConfiguratorService(cpuService, gpuService, ssdService, hddService, ramService, pcCaseService, powerSupplyService, motherboardService);

    @Test
    void getAllComponentsIdsAndNamesWithPrices_shouldReturnAllComponentsWithPrices() {
        // Arrange
        LinkedHashMap<String, String> cpuPrices = new LinkedHashMap<>();
        cpuPrices.put("cpuId", "AMD Ryzen 7 3700X ($299.99)");
        LinkedHashMap<String, String> gpuPrices = new LinkedHashMap<>();
        gpuPrices.put("gpuId", "Nvidia RTX 3080 ($699.99)");
        LinkedHashMap<String, String> mbPrices = new LinkedHashMap<>();
        mbPrices.put("mbId", "ASUS ROG Strix B450-F ($129.99)");
        LinkedHashMap<String, String> ramPrices = new LinkedHashMap<>();
        ramPrices.put("ramId", "Corsair Vengeance LPX 16GB ($79.99)");
        LinkedHashMap<String, String> ssdPrices = new LinkedHashMap<>();
        ssdPrices.put("ssdId", "Samsung 970 Evo 1TB ($149.99)");
        LinkedHashMap<String, String> hddPrices = new LinkedHashMap<>();
        hddPrices.put("hddId", "Seagate Barracuda 2TB ($54.99)");
        LinkedHashMap<String, String> psPrices = new LinkedHashMap<>();
        psPrices.put("psId", "Corsair RM850x ($119.99)");
        LinkedHashMap<String, String> casePrices = new LinkedHashMap<>();
        casePrices.put("caseId", "NZXT H510 ($69.99)");

        when(cpuService.getAllNamesWithPrices()).thenReturn(cpuPrices);
        when(gpuService.getAllNamesWithPrices()).thenReturn(gpuPrices);
        when(motherboardService.getAllNamesWithPrices()).thenReturn(mbPrices);
        when(ramService.getAllNamesWithPrices()).thenReturn(ramPrices);
        when(ssdService.getAllNamesWithPrices()).thenReturn(ssdPrices);
        when(hddService.getAllNamesWithPrices()).thenReturn(hddPrices);
        when(powerSupplyService.getAllNamesWithPrices()).thenReturn(psPrices);
        when(pcCaseService.getAllNamesWithPrices()).thenReturn(casePrices);

        List<LinkedHashMap<String, String>> expectedComponents = List.of(cpuPrices, gpuPrices, mbPrices, ramPrices, ssdPrices, hddPrices, psPrices, casePrices);

        // Act
        ConfiguratorComponents actualComponents = configuratorService.getAllComponentsIdsAndNamesWithPrices();

        // Assert
        verify(cpuService).getAllNamesWithPrices();
        verify(gpuService).getAllNamesWithPrices();
        verify(motherboardService).getAllNamesWithPrices();
        verify(ramService).getAllNamesWithPrices();
        verify(ssdService).getAllNamesWithPrices();
        verify(hddService).getAllNamesWithPrices();
        verify(powerSupplyService).getAllNamesWithPrices();
        verify(pcCaseService).getAllNamesWithPrices();

        assertEquals(new ConfiguratorComponents(expectedComponents), actualComponents);
    }
}
