package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.pc.specs.GPU;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.repository.pc.hardware.GpuRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class GpuServiceTest extends BaseServiceTest<GPU, GpuService, GpuRepository> {
    private final GpuRepository mockGpuRepository = mock(GpuRepository.class);
    private final GPU testGpu = new GPU(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23
    );

    private final GpuService gpuService = new GpuService(mockGpuRepository);

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
        when(mockGpuRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockGpuRepository).existsById("testId");
        verifyNoMoreInteractions(mockGpuRepository);
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
        GPU expected = testGpu.withPhotos(List.of("Test"));

        when(mockGpuRepository.findById("testId")).thenReturn(Optional.of(testGpu));
        when(mockGpuRepository.save(testGpu.withGpuPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        GPU actualGpu = gpuService.attachPhoto("testId", "Test");

        assertEquals(actualGpu.gpuPhotos(), expected.gpuPhotos());
        verify(mockGpuRepository).findById("testId");
        verify(mockGpuRepository).save(testGpu.withGpuPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockGpuRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> gpuService.attachPhoto("testId", "TEST"));
        verify(mockGpuRepository).findById("testId");
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
