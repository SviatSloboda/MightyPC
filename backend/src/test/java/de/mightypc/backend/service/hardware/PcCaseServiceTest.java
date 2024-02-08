package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PcCaseServiceTest {
    private final PcCaseRepository pcCaseRepository = mock(PcCaseRepository.class);
    private final PcCaseService pcCaseService = new PcCaseService(pcCaseRepository);

    private static PcCase getPcCase() {
        return new PcCase(new HardwareSpec(
                "1",
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                "10x10x10"
        );
    }

    @Test
    void getAll_ReturnsAllPcCases() {
        // Arrange
        List<PcCase> expected = new ArrayList<>(List.of(getPcCase()));
        when(pcCaseRepository.findAll()).thenReturn(expected);

        // Act
        List<PcCase> actual = pcCaseService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all PcCases");
        verify(pcCaseRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoPcCases() {
        // Arrange
        List<PcCase> expected = new ArrayList<>();
        when(pcCaseRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, pcCaseService::getAll);
    }

    @Test
    void getById_ReturnsPcCaseWhenExists() {
        //Arrange
        PcCase expected = getPcCase();
        when(pcCaseRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        PcCase actual = pcCaseService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the PcCase when it exists");
        verify(pcCaseRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenPcCaseDoesNotExist() {
        //Arrange
        when(pcCaseRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> pcCaseService.getById("1"),
                "getById should throw HardwareNotFoundException when the PcCase does not exist");
        verify(pcCaseRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedPcCase() {
        // Arrange
        PcCase expected = getPcCase();
        when(pcCaseRepository.save(expected)).thenReturn(expected);

        // Act
        PcCase actual = pcCaseService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved PcCase");
        verify(pcCaseRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedPcCaseWhenPcCaseExists() {
        // Arrange
        PcCase expected = getPcCase();
        when(pcCaseRepository.existsById("1")).thenReturn(true);
        when(pcCaseRepository.save(expected)).thenReturn(expected);

        // Act
        PcCase actual = pcCaseService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated PcCase");
        verify(pcCaseRepository).save(expected);
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenPcCaseDoesNotExist() {
        // Arrange
        PcCase expected = getPcCase();
        when(pcCaseRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> pcCaseService.update(expected),
                "update should throw HardwareNotFoundException when the PcCase does not exist");
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesPcCaseWhenExists() {
        // Arrange
        String id = "1";
        when(pcCaseRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = pcCaseService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the PcCase exists and is deleted");
        verify(pcCaseRepository).deleteById(id);
        verify(pcCaseRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenPcCaseDoesNotExist() {
        // Arrange
        String id = "1";
        when(pcCaseRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> pcCaseService.deleteById(id), "Expected HardwareNotFoundException when PcCase does not exist");
        verify(pcCaseRepository).existsById(id);
    }
}
