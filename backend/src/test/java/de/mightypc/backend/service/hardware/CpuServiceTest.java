package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
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

class CpuServiceTest {
    private final CpuRepository cpuRepository = mock(CpuRepository.class);
    private final CpuService cpuService = new CpuService(cpuRepository);

    @Test
    void getAll_ReturnsAllCpus() {
        // Arrange
        List<CPU> expected = new ArrayList<>(List.of(new CPU("test", "test", 1.01f, 1, 1, 1.01f)));
        when(cpuRepository.findAll()).thenReturn(expected);

        // Act
        List<CPU> actual = cpuService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all CPUs");
        verify(cpuRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoCpus() {
        // Arrange
        List<CPU> expected = new ArrayList<>();
        when(cpuRepository.findAll()).thenReturn(expected);

        // Act
        List<CPU> actual = cpuService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no CPUs exist");
        verify(cpuRepository).findAll();
    }

    @Test
    void getById_ReturnsCpuWhenExists() {
        //Arrange
        Optional<CPU> expected = Optional.of(new CPU("1", "test", "test", 1.01f, 1, 1, 1.01f));
        when(cpuRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<CPU> actual = cpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the CPU when it exists");
        verify(cpuRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenCpuDoesNotExist() {
        //Arrange
        Optional<CPU> expected = Optional.empty();
        when(cpuRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<CPU> actual = cpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the CPU does not exist");
        verify(cpuRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenCpuIsPersisted() {
        // Arrange
        CPU cpuToSave = new CPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(cpuRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = cpuService.save(cpuToSave);

        // Assert
        assertTrue(result, "save should return true when the CPU is persisted");
        verify(cpuRepository).save(cpuToSave);
        verify(cpuRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenCpuIsNotPersisted() {
        // Arrange
        CPU cpuToSave = new CPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(cpuRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = cpuService.save(cpuToSave);

        // Assert
        assertFalse(result, "save should return false when the CPU is not persisted");
        verify(cpuRepository).save(cpuToSave);
        verify(cpuRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenCpuExists() {
        // Arrange
        CPU cpuToUpdate = new CPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(cpuRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = cpuService.update(cpuToUpdate);

        // Assert
        assertTrue(result, "update should return true when the CPU exists and is updated");
        verify(cpuRepository).save(cpuToUpdate);
        verify(cpuRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenCpuDoesNotExist() {
        // Arrange
        CPU cpuToUpdate = new CPU("1", "test", "test", 1.01f, 1, 1, 1.01f);
        when(cpuRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = cpuService.update(cpuToUpdate);

        // Assert
        assertFalse(result, "update should return false when the CPU does not exist");
        verify(cpuRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenCpuIsDeleted() {
        // Arrange
        String id = "1";
        when(cpuRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = cpuService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the CPU exists and is deleted");
        verify(cpuRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenCpuDoesNotExist() {
        // Arrange
        String id = "1";
        when(cpuRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = cpuService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the CPU does not exist");
        verify(cpuRepository).existsById(id);
        verifyNoMoreInteractions(cpuRepository);
    }
}
