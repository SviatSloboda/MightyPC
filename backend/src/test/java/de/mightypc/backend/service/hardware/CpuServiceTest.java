package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.CpuNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class CpuServiceTest extends BaseServiceTest<CPU, CpuService, CpuRepository, CpuNotFoundException> {
    private final CpuRepository mockCpuRepository = mock(CpuRepository.class);
    private final CpuService cpuService = new CpuService(mockCpuRepository);

    private final CPU testCpu = new CPU(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            "testSocket"
    );

    private final CPU testCpu2 = new CPU(
            "testId2",
            new HardwareSpec("test2", "test2", new BigDecimal(333), 4.99f),
            240,
            "testSocket2"
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<CPU> cpus = new ArrayList<>(List.of(testCpu, testCpu2));

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        CPU expected = testCpu.withEnergyConsumption(999);

        when(mockCpuRepository.existsById("testId")).thenReturn(true);
        when(mockCpuRepository.save(expected)).thenReturn(expected);

        // Act
        CPU actual = service.update(expected);

        // Assert
        verify(mockCpuRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        CPU expected = testCpu.withEnergyConsumption(999);
        when(mockCpuRepository.existsById("testId")).thenReturn(false);
        when(mockCpuRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(CpuNotFoundException.class, () -> service.update(expected));
        verify(mockCpuRepository).existsById("testId");
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        // Arrange
        CPU expected = testCpu.withPhotos(List.of("Test"));

        when(mockCpuRepository.findById("testId")).thenReturn(Optional.of(testCpu));
        when(mockCpuRepository.save(testCpu.withCpuPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        CPU actualCpu = cpuService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualCpu.cpuPhotos(), expected.cpuPhotos());
        verify(mockCpuRepository).findById("testId");
        verify(mockCpuRepository).save(testCpu.withCpuPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CpuNotFoundException.class, () -> cpuService.attachPhoto("testId", "TEST"));
        verify(mockCpuRepository).findById("testId");
    }

    @Test
    void getSocketOfCpuById_shouldGiveCorrectSocket() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.of(testCpu));

        // Act
        String actual = cpuService.getSocketOfCpuById("testId");

        // Assert
        verify(mockCpuRepository).findById("testId");
        assertEquals(testCpu.socket(), actual);
    }

    @Test
    void getSocketOfCpuById_shouldThrowHardwareNotFoundException_whenCpuDoesNotExistInRepository() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(CpuNotFoundException.class, () -> cpuService.getSocketOfCpuById("testId"));
        verify(mockCpuRepository).findById("testId");
    }

    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testCpu);

        // Assert
        assertEquals(testCpu.hardwareSpec().name(), actual);
    }

    @Test
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        String expected = "$cpus:\n{testId:test:($666)}\n{testId2:test2:($333)}\n";

        // Act
        String actual = cpuService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = cpuService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "cpu"),
                new ItemForConfigurator("testId2", "test2", new BigDecimal(333), "", "cpu")
        );

        // Act
        List<ItemForConfigurator> actual = cpuService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getCpus_shouldReturnFilteredAndSortedCpusByRatingDesc() {
        // Arrange
        List<CPU> expected = List.of(testCpu2, testCpu);
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        // Act
        Page<CPU> actual = cpuService.getCpus(pageable, "rating-desc", null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getCpus_shouldReturnFilteredAndSortedCpusByRatingAsc() {
        // Arrange
        List<CPU> expected = List.of(testCpu, testCpu2);
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        // Act
        Page<CPU> actual = cpuService.getCpus(pageable, "rating-asc", null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getCpus_shouldReturnFilteredAndSortedCpusByPriceAsc() {
        // Arrange
        List<CPU> expected = List.of(testCpu2, testCpu);
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        // Act
        Page<CPU> actual = cpuService.getCpus(pageable, "price-asc", null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockCpuRepository).findAll();
    }


    @Test
    void getCpus_shouldReturnFilteredAndSortedCpus() {
        // Arrange
        List<CPU> expected = List.of(testCpu);
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        // Act
        Page<CPU> actual = cpuService.getCpus(pageable, "price-asc", "testSocket", 500, 700);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockCpuRepository).findAll();
    }

    @Test
    void getCpus_shouldReturnFilteredAndSortedCpusByPriceDesc() {
        // Arrange
        List<CPU> expected = List.of(testCpu, testCpu2);
        when(mockCpuRepository.findAll()).thenReturn(cpus);

        // Act
        Page<CPU> actual = cpuService.getCpus(pageable, "price-desc", null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockCpuRepository).findAll();
    }


    @Override
    protected CpuNotFoundException getException() {
        return new CpuNotFoundException("There is no such Cpu with id: ");
    }

    @Override
    protected CpuRepository getMockRepository() {
        return mockCpuRepository;
    }

    @Override
    protected CpuService getService(CpuRepository repository) {
        return new CpuService(repository);
    }

    @Override
    protected CPU getEntity() {
        return testCpu;
    }
}
