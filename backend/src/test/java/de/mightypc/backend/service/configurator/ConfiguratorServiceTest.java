package de.mightypc.backend.service.configurator;

import de.mightypc.backend.model.configurator.ConfiguratorItems;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.service.hardware.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ConfiguratorServiceTest {
    private final CpuService cpuService = mock(CpuService.class);
    private final GpuService gpuService = mock(GpuService.class);
    private final MotherboardService motherboardService = mock(MotherboardService.class);
    private final SsdService ssdService = mock(SsdService.class);
    private final HddService hddService = mock(HddService.class);
    private final RamService ramService = mock(RamService.class);
    private final PcCaseService pcCaseService = mock(PcCaseService.class);
    private final PowerSupplyService powerSupplyService = mock(PowerSupplyService.class);

    private final ConfiguratorService configuratorService = new ConfiguratorService(
            cpuService, gpuService, ssdService, hddService, ramService, pcCaseService, powerSupplyService, motherboardService
    );

    @Test
    void getAllComponentsIdsAndNamesWithPrices_ForChatGpt_shouldReturnProperString() {
        when(cpuService.getAllNamesWithPrices()).thenReturn("cpu1 100 cpu2 200 ");
        when(gpuService.getAllNamesWithPrices()).thenReturn("gpu1 300 gpu2 400 ");
        when(motherboardService.getAllNamesWithPrices()).thenReturn("mb1 500 mb2 600 ");
        when(ssdService.getAllNamesWithPrices()).thenReturn("ssd1 700 ssd2 800 ");
        when(hddService.getAllNamesWithPrices()).thenReturn("hdd1 900 hdd2 1000 ");
        when(ramService.getAllNamesWithPrices()).thenReturn("ram1 1100 ram2 1200 ");
        when(pcCaseService.getAllNamesWithPrices()).thenReturn("case1 1300 case2 1400 ");
        when(powerSupplyService.getAllNamesWithPrices()).thenReturn("ps1 1500 ps2 1600 ");

        String actual = configuratorService.getAllComponentsIdsAndNamesWithPricesForChatGpt();
        String expected = "cpu1100cpu2200gpu1300gpu2400mb1500mb2600ram11100ram21200ssd1700ssd2800hdd1900hdd21000ps11500ps21600case11300case21400";

        assertEquals(expected, actual);
    }

    @Test
    void getAllItemsWithInfoForOrder_shouldReturnProperConfiguratorItems() {
        List<ItemForConfigurator> cpuItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> gpuItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> motherboardItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> ramItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> ssdItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> hddItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> powerSupplyItems = List.of(mock(ItemForConfigurator.class));
        List<ItemForConfigurator> pcCaseItems = List.of(mock(ItemForConfigurator.class));

        when(cpuService.getAllHardwareInfoForConfiguration()).thenReturn(cpuItems);
        when(gpuService.getAllHardwareInfoForConfiguration()).thenReturn(gpuItems);
        when(motherboardService.getAllHardwareInfoForConfiguration()).thenReturn(motherboardItems);
        when(ramService.getAllHardwareInfoForConfiguration()).thenReturn(ramItems);
        when(ssdService.getAllHardwareInfoForConfiguration()).thenReturn(ssdItems);
        when(hddService.getAllHardwareInfoForConfiguration()).thenReturn(hddItems);
        when(powerSupplyService.getAllHardwareInfoForConfiguration()).thenReturn(powerSupplyItems);
        when(pcCaseService.getAllHardwareInfoForConfiguration()).thenReturn(pcCaseItems);

        ConfiguratorItems actual = configuratorService.getAllItemsWithInfoForConfigurator();
        ConfiguratorItems expected = new ConfiguratorItems(List.of(
                cpuItems, gpuItems, motherboardItems, ramItems, ssdItems, hddItems, powerSupplyItems, pcCaseItems
        ));

        assertEquals(expected, actual);
    }
}
