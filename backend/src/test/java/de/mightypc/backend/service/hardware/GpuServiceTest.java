package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

class GpuServiceTest {
    private final GpuRepository gpuRepository = mock(GpuRepository.class);
    private final GpuService gpuService = new GpuService(gpuRepository);

    private static GPU getGpu() {
        return new GPU("1", new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                9500,
                125
        );
    }

    @Test
    void getAll_ReturnsAllGpus() {
        // Arrange
        List<GPU> expected = new ArrayList<>(List.of(getGpu()));
        when(gpuRepository.findAll()).thenReturn(expected);

        // Act
        List<GPU> actual = gpuService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all GPUs");
        verify(gpuRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoGpus() {
        // Arrange
        List<GPU> expected = new ArrayList<>();
        when(gpuRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, gpuService::getAll);
    }

    @Test
    void getById_ReturnsGpuWhenExists() {
        //Arrange
        GPU expected = getGpu();
        when(gpuRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        GPU actual = gpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the GPU when it exists");
        verify(gpuRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenGpuDoesNotExist() {
        //Arrange
        when(gpuRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> gpuService.getById("1"),
                "getById should throw HardwareNotFoundException when the GPU does not exist");
        verify(gpuRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedGpu() {
        // Arrange
        GPU expected = getGpu();
        when(gpuRepository.save(expected)).thenReturn(expected);

        // Act
        GPU actual = gpuService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved GPU");
        verify(gpuRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedGpuWhenGpuExists() {
        // Arrange
        GPU expected = getGpu();
        when(gpuRepository.existsById("1")).thenReturn(true);
        when(gpuRepository.save(expected)).thenReturn(expected);

        // Act
        GPU actual = gpuService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated GPU");
        verify(gpuRepository).save(expected);
        verify(gpuRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenGpuDoesNotExist() {
        // Arrange
        GPU expected = getGpu();
        when(gpuRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> gpuService.update(expected),
                "update should throw HardwareNotFoundException when the GPU does not exist");
        verify(gpuRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesGpuWhenExists() {
        // Arrange
        String id = "1";
        when(gpuRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = gpuService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the GPU exists and is deleted");
        verify(gpuRepository).deleteById(id);
        verify(gpuRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenGpuDoesNotExist() {
        // Arrange
        String id = "1";
        when(gpuRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> gpuService.deleteById(id), "Expected HardwareNotFoundException when GPU does not exist");
        verify(gpuRepository).existsById(id);
    }

    @Test
    void attachPhoto_WhenGpuExists_ThenPhotoIsAttached() {
        // Arrange
        String gpuId = "1";
        String photoUrl = "https://example.com/photo.jpg";
        GPU gpuBeforeUpdate = new GPU("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), 9500, 125, new ArrayList<>());
        Optional<GPU> optionalGpuBeforeUpdate = Optional.of(gpuBeforeUpdate);

        List<String> photosWithNewUrl = new ArrayList<>();
        photosWithNewUrl.add(photoUrl);
        GPU gpuAfterUpdate = new GPU("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), 9500, 125, photosWithNewUrl);

        when(gpuRepository.findById(gpuId)).thenReturn(optionalGpuBeforeUpdate);
        when(gpuRepository.save(any(GPU.class))).thenReturn(gpuAfterUpdate);

        // Act
        gpuService.attachPhoto(gpuId, photoUrl);

        // Assert
        verify(gpuRepository).findById(gpuId);
        verify(gpuRepository).save(gpuAfterUpdate);
    }


    @Test
    void attachPhoto_WhenGpuDoesNotExist_ThenNoActionTaken() {
        // Arrange
        String gpuId = "nonExistingId";
        String photoUrl = "https://example.com/photo.jpg";
        when(gpuRepository.findById(gpuId)).thenReturn(Optional.empty());

        // Act
        gpuService.attachPhoto(gpuId, photoUrl);

        // Assert
        verify(gpuRepository).findById(gpuId);
    }
}
