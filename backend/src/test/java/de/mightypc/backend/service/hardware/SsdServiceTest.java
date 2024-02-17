package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SsdServiceTest {
    private final SsdRepository ssdRepository = mock(SsdRepository.class);
    private final SsdService ssdService = new SsdService(ssdRepository);

    private static SSD getSsd() {
        return new SSD("1", new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                5,
                950
        );
    }

    @Test
    void getAll_ReturnsAllSsds() {
        // Arrange
        List<SSD> expected = new ArrayList<>(List.of(getSsd()));
        when(ssdRepository.findAll()).thenReturn(expected);

        // Act
        List<SSD> actual = ssdService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all SSDs");
        verify(ssdRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoSsds() {
        // Arrange
        List<SSD> expected = new ArrayList<>();
        when(ssdRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, ssdService::getAll);
    }

    @Test
    void getById_ReturnsSsdWhenExists() {
        //Arrange
        SSD expected = getSsd();
        when(ssdRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        SSD actual = ssdService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the SSD when it exists");
        verify(ssdRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenSsdDoesNotExist() {
        //Arrange
        when(ssdRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ssdService.getById("1"),
                "getById should throw HardwareNotFoundException when the SSD does not exist");
        verify(ssdRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedSsd() {
        // Arrange
        SSD expected = getSsd();
        when(ssdRepository.save(expected)).thenReturn(expected);

        // Act
        SSD actual = ssdService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved SSD");
        verify(ssdRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedSsdWhenSsdExists() {
        // Arrange
        SSD expected = getSsd();
        when(ssdRepository.existsById("1")).thenReturn(true);
        when(ssdRepository.save(expected)).thenReturn(expected);

        // Act
        SSD actual = ssdService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated SSD");
        verify(ssdRepository).save(expected);
        verify(ssdRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenSsdDoesNotExist() {
        // Arrange
        SSD expected = getSsd();
        when(ssdRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ssdService.update(expected),
                "update should throw HardwareNotFoundException when the SSD does not exist");
        verify(ssdRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesSsdWhenExists() {
        // Arrange
        String id = "1";
        when(ssdRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = ssdService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the SSD exists and is deleted");
        verify(ssdRepository).deleteById(id);
        verify(ssdRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenSsdDoesNotExist() {
        // Arrange
        String id = "1";
        when(ssdRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ssdService.deleteById(id), "Expected HardwareNotFoundException when SSD does not exist");
        verify(ssdRepository).existsById(id);
    }
}
