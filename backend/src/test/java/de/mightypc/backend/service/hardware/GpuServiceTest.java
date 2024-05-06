package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.GpuNotFoundException;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockGpuRepository.findAll()).thenReturn(List.of(testGpu));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockGpuRepository).findAll();
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
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(List.of(testGpu, testGpu2), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(List.of(testGpu2, testGpu), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(List.of(testGpu, testGpu2), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllGpusWithProperSorting() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(List.of(testGpu2, testGpu), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(Collections.singletonList(testGpu2), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllGpusWithProperFiltering() {
        // Arrange
        Page<GPU> expected = new PageImpl<>(Collections.singletonList(testGpu), pageable, 8);
        when(repository.findAll()).thenReturn(gpus);

        // Act
        Page<GPU> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
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
