package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CpuServiceTest {
    private final CpuRepository cpuRepository = mock(CpuRepository.class);
    private final CpuService cpuService = new CpuService(cpuRepository);

    private static CPU getCpu() {
        return new CPU(new HardwareSpec(
                "1",
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                9500,
                125
        );
    }

    @Test
    void getAll_ReturnsAllCpus() {
        // Arrange
        List<CPU> expected = new ArrayList<>(List.of(getCpu()));
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

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, cpuService::getAll);
    }

    @Test
    void getById_ReturnsCpuWhenExists() {
        //Arrange
        CPU expected = getCpu();
        when(cpuRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        CPU actual = cpuService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the CPU when it exists");
        verify(cpuRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenCpuDoesNotExist() {
        //Arrange
        when(cpuRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> cpuService.getById("1"),
                "getById should throw HardwareNotFoundException when the CPU does not exist");
        verify(cpuRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedCpu() {
        // Arrange
        CPU expected = getCpu();
        when(cpuRepository.save(expected)).thenReturn(expected);

        // Act
        CPU actual = cpuService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved CPU");
        verify(cpuRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedCpuWhenCpuExists() {
        // Arrange
        CPU expected = getCpu();
        when(cpuRepository.existsById("1")).thenReturn(true);
        when(cpuRepository.save(expected)).thenReturn(expected);

        // Act
        CPU actual = cpuService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated CPU");
        verify(cpuRepository).save(expected);
        verify(cpuRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenCpuDoesNotExist() {
        // Arrange
        CPU expected = getCpu();
        when(cpuRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> cpuService.update(expected),
                "update should throw HardwareNotFoundException when the CPU does not exist");
        verify(cpuRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesCpuWhenExists() {
        // Arrange
        String id = "1";
        when(cpuRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = cpuService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the CPU exists and is deleted");
        verify(cpuRepository).deleteById(id);
        verify(cpuRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenCpuDoesNotExist() {
        // Arrange
        String id = "1";
        when(cpuRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> cpuService.deleteById(id), "Expected HardwareNotFoundException when CPU does not exist");
        verify(cpuRepository).existsById(id);
    }
}
