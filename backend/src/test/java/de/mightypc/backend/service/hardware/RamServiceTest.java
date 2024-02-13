package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RamServiceTest {
    private final RamRepository ramRepository = mock(RamRepository.class);
    private final RamService ramService = new RamService(ramRepository);

    private static RAM getRam() {
        return new RAM(new HardwareSpec(
                "1",
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                "ddr2",
                123,
                125
        );
    }

    @Test
    void getAll_ReturnsAllRams() {
        // Arrange
        List<RAM> expected = new ArrayList<>(List.of(getRam()));
        when(ramRepository.findAll()).thenReturn(expected);

        // Act
        List<RAM> actual = ramService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all RAMs");
        verify(ramRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoRams() {
        // Arrange
        List<RAM> expected = new ArrayList<>();
        when(ramRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, ramService::getAll);
    }

    @Test
    void getById_ReturnsRamWhenExists() {
        //Arrange
        RAM expected = getRam();
        when(ramRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        RAM actual = ramService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the RAM when it exists");
        verify(ramRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenRamDoesNotExist() {
        //Arrange
        when(ramRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.getById("1"),
                "getById should throw HardwareNotFoundException when the RAM does not exist");
        verify(ramRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedRam() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.save(expected)).thenReturn(expected);

        // Act
        RAM actual = ramService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved RAM");
        verify(ramRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedRamWhenRamExists() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.existsById("1")).thenReturn(true);
        when(ramRepository.save(expected)).thenReturn(expected);

        // Act
        RAM actual = ramService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated RAM");
        verify(ramRepository).save(expected);
        verify(ramRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenRamDoesNotExist() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.update(expected),
                "update should throw HardwareNotFoundException when the RAM does not exist");
        verify(ramRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesRamWhenExists() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = ramService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the RAM exists and is deleted");
        verify(ramRepository).deleteById(id);
        verify(ramRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenRamDoesNotExist() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.deleteById(id), "Expected HardwareNotFoundException when RAM does not exist");
        verify(ramRepository).existsById(id);
    }
}
