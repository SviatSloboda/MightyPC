package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class GpuServiceTest {
    private final GpuRepository gpuRepository = mock(GpuRepository.class);
    private final GpuService gpuService = new GpuService(gpuRepository);

    @Test
    void getAll_ReturnsAllGpus() {
        // Arrange
        List<GPU> expected = new ArrayList<>(List.of(new GPU("test", "test", 1.01f, 1, 1, 1.01f)));
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

        // Act
        List<GPU> actual = gpuService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no GPUs exist");
        verify(gpuRepository).findAll();
    }

    @Test
    void getById_ReturnsGpuWhenExists() {
        //Arrange
        Optional<GPU> expected = Optional.of(new GPU("1", "test", "test", 1.01f, 1, 1, 1.01f));
        when(gpuRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<GPU> actual = gpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the GPU when it exists");
        verify(gpuRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenGpuDoesNotExist() {
        //Arrange
        Optional<GPU> expected = Optional.empty();
        when(gpuRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<GPU> actual = gpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the GPU does not exist");
        verify(gpuRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenGpuIsPersisted() {
        // Arrange
        GPU gpuToSave = new GPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(gpuRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = gpuService.save(gpuToSave);

        // Assert
        assertTrue(result, "save should return true when the GPU is persisted");
        verify(gpuRepository).save(gpuToSave);
        verify(gpuRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenGpuIsNotPersisted() {
        // Arrange
        GPU gpuToSave = new GPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(gpuRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = gpuService.save(gpuToSave);

        // Assert
        assertFalse(result, "save should return false when the GPU is not persisted");
        verify(gpuRepository).save(gpuToSave);
        verify(gpuRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenGpuExists() {
        // Arrange
        GPU gpuToUpdate = new GPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(gpuRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = gpuService.update(gpuToUpdate);

        // Assert
        assertTrue(result, "update should return true when the GPU exists and is updated");
        verify(gpuRepository).save(gpuToUpdate);
        verify(gpuRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenGpuDoesNotExist() {
        // Arrange
        GPU gpuToUpdate = new GPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(gpuRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = gpuService.update(gpuToUpdate);

        // Assert
        assertFalse(result, "update should return false when the GPU does not exist");
        verify(gpuRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenGpuIsDeleted() {
        // Arrange
        String id = "1";
        when(gpuRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = gpuService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the GPU exists and is deleted");
        verify(gpuRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenGpuDoesNotExist() {
        // Arrange
        String id = "1";
        when(gpuRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = gpuService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the GPU does not exist");
        verify(gpuRepository).existsById(id);
        verifyNoMoreInteractions(gpuRepository);
    }
}
