package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.GpuNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class GpuServiceTest extends BaseServiceTest<GPU, GpuService, GpuRepository, GpuNotFoundException> {
    private final GpuRepository mockGpuRepository = mock(GpuRepository.class);
    private final GpuService gpuService = new GpuService(mockGpuRepository);

    private final GPU testGpu = new GPU(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23
    );

    private final GPU testGpu2 = new GPU(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 4.29f),
            249
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<GPU> gpus = new ArrayList<>(List.of(testGpu, testGpu2));


    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        GPU expected = testGpu.withEnergyConsumption(999);

        when(mockGpuRepository.existsById("testId")).thenReturn(true);
        when(mockGpuRepository.save(expected)).thenReturn(expected);

        // Act
        GPU actual = service.update(expected);

        // Assert
        verify(mockGpuRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        GPU expected = testGpu.withEnergyConsumption(999);
        when(mockGpuRepository.existsById("testId")).thenReturn(false);

        // Act & Assert
        assertThrows(GpuNotFoundException.class, () -> service.update(expected));
        verify(mockGpuRepository).existsById("testId");
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        // Arrange
        GPU expected = testGpu.withPhotos(List.of("Test"));

        when(mockGpuRepository.findById("testId")).thenReturn(Optional.of(testGpu));
        when(mockGpuRepository.save(testGpu.withGpuPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        GPU actualGpu = gpuService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualGpu.gpuPhotos(), expected.gpuPhotos());
        verify(mockGpuRepository).findById("testId");
        verify(mockGpuRepository).save(testGpu.withGpuPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockGpuRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GpuNotFoundException.class, () -> gpuService.attachPhoto("testId", "TEST"));
        verify(mockGpuRepository).findById("testId");
    }


    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testGpu);

        // Assert
        assertEquals(testGpu.hardwareSpec().name(), actual);
    }

    @Test
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        String expected = "$gpus:\n{testId:test:($666)}\n{testId2:test:($333)}\n";

        // Act
        String actual = gpuService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = gpuService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "gpu"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "gpu")
        );

        // Act
        List<ItemForConfigurator> actual = gpuService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldReturnFilteredAndSortedGpus() {
        // Arrange
        List<GPU> expected = List.of(testGpu);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "price-asc", 500, 700, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldReturnFilteredAndSortedGpusByEnergyConsumption() {
        // Arrange
        List<GPU> expected = List.of(testGpu2);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "rating-desc", null, null, 200, 300);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldSortGpusByPriceDesc() {
        // Arrange
        List<GPU> expected = List.of(testGpu, testGpu2);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "price-desc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldSortGpusByRatingAsc() {
        // Arrange
        List<GPU> expected = List.of(testGpu, testGpu2);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "rating-asc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnInfoWithPhotos() {
        // Arrange
        GPU gpuWithPhoto = testGpu.withGpuPhotos(List.of("photoUrl"));
        List<GPU> gpusWithPhoto = List.of(gpuWithPhoto, testGpu2);
        when(mockGpuRepository.findAll()).thenReturn(gpusWithPhoto);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "photoUrl", "gpu"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "gpu")
        );

        // Act
        List<ItemForConfigurator> actual = gpuService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldSortGpusByRatingDesc() {
        // Arrange
        List<GPU> expected = List.of(testGpu2, testGpu);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "rating-desc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldFilterGpusByLowestAndHighestPrice() {
        // Arrange
        List<GPU> expected = List.of(testGpu2);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "price-asc", 300, 400, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockGpuRepository).findAll();
    }

    @Test
    void getGpus_shouldFilterAndSortGpusByEnergyConsumptionAndRatingAsc() {
        // Arrange
        List<GPU> expected = List.of(testGpu);
        when(mockGpuRepository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = gpuService.getGpus(pageable, "rating-asc", null, null, 20, 30);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockGpuRepository).findAll();
    }


    @Override
    protected GpuNotFoundException getException() {
        return new GpuNotFoundException("There is no such Gpu!");
    }

    @Override
    protected GpuRepository getMockRepository() {
        return mockGpuRepository;
    }

    @Override
    protected GpuService getService(GpuRepository repository) {
        return new GpuService(repository);
    }

    @Override
    protected GPU getEntity() {
        return testGpu;
    }
}
