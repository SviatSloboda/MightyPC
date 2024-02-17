package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HddServiceTest {
    private final HddRepository hddRepository = mock(HddRepository.class);
    private final HddService hddService = new HddService(hddRepository);

    private static HDD getHdd() {
        return new HDD("1",new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                9500,
                125
        );
    }

    @Test
    void getAll_ReturnsAllHdds() {
        // Arrange
        List<HDD> expected = new ArrayList<>(List.of(getHdd()));
        when(hddRepository.findAll()).thenReturn(expected);

        // Act
        List<HDD> actual = hddService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all HDDs");
        verify(hddRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoHdds() {
        // Arrange
        List<HDD> expected = new ArrayList<>();
        when(hddRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, hddService::getAll);
    }

    @Test
    void getById_ReturnsHddWhenExists() {
        //Arrange
        HDD expected = getHdd();
        when(hddRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        HDD actual = hddService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the HDD when it exists");
        verify(hddRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenHddDoesNotExist() {
        //Arrange
        when(hddRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> hddService.getById("1"),
                "getById should throw HardwareNotFoundException when the HDD does not exist");
        verify(hddRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedHdd() {
        // Arrange
        HDD expected = getHdd();
        when(hddRepository.save(expected)).thenReturn(expected);

        // Act
        HDD actual = hddService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved HDD");
        verify(hddRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedHddWhenHddExists() {
        // Arrange
        HDD expected = getHdd();
        when(hddRepository.existsById("1")).thenReturn(true);
        when(hddRepository.save(expected)).thenReturn(expected);

        // Act
        HDD actual = hddService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated HDD");
        verify(hddRepository).save(expected);
        verify(hddRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenHddDoesNotExist() {
        // Arrange
        HDD expected = getHdd();
        when(hddRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> hddService.update(expected),
                "update should throw HardwareNotFoundException when the HDD does not exist");
        verify(hddRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesHddWhenExists() {
        // Arrange
        String id = "1";
        when(hddRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = hddService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the HDD exists and is deleted");
        verify(hddRepository).deleteById(id);
        verify(hddRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenHddDoesNotExist() {
        // Arrange
        String id = "1";
        when(hddRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> hddService.deleteById(id), "Expected HardwareNotFoundException when HDD does not exist");
        verify(hddRepository).existsById(id);
    }
}
