package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
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

class PcCaseServiceTest {
    private final PcCaseRepository pcCaseRepository = mock(PcCaseRepository.class);
    private final PcCaseService pcCaseService = new PcCaseService(pcCaseRepository);

    @Test
    void getAll_ReturnsAllPcCases() {
        // Arrange
        List<PcCase> expected = new ArrayList<>(List.of(new PcCase("1", "test", 1.01f, 1.01f)));
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

        // Act
        List<PcCase> actual = pcCaseService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no PcCases exist");
        verify(pcCaseRepository).findAll();
    }

    @Test
    void getById_ReturnsPcCaseWhenExists() {
        //Arrange
        Optional<PcCase> expected = Optional.of(new PcCase("1", "test", 1.01f, 1.01f));
        when(pcCaseRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<PcCase> actual = pcCaseService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the PcCase when it exists");
        verify(pcCaseRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenPcCaseDoesNotExist() {
        //Arrange
        Optional<PcCase> expected = Optional.empty();
        when(pcCaseRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<PcCase> actual = pcCaseService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the PcCase does not exist");
        verify(pcCaseRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenPcCaseIsPersisted() {
        // Arrange
        PcCase pcCaseToSave = new PcCase("1", "test", 1.01f, 1.01f);
        when(pcCaseRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = pcCaseService.save(pcCaseToSave);

        // Assert
        assertTrue(result, "save should return true when the PcCase is persisted");
        verify(pcCaseRepository).save(pcCaseToSave);
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenPcCaseIsNotPersisted() {
        // Arrange
        PcCase pcCaseToSave = new PcCase("1", "test", 1.01f, 1.01f);
        when(pcCaseRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = pcCaseService.save(pcCaseToSave);

        // Assert
        assertFalse(result, "save should return false when the PcCase is not persisted");
        verify(pcCaseRepository).save(pcCaseToSave);
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenPcCaseExists() {
        // Arrange
        PcCase pcCaseToUpdate = new PcCase("1", "test", 1.01f, 1.01f);
        when(pcCaseRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = pcCaseService.update(pcCaseToUpdate);

        // Assert
        assertTrue(result, "update should return true when the PcCase exists and is updated");
        verify(pcCaseRepository).save(pcCaseToUpdate);
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenPcCaseDoesNotExist() {
        // Arrange
        PcCase pcCaseToUpdate = new PcCase("1", "test", 1.01f, 1.01f);
        when(pcCaseRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = pcCaseService.update(pcCaseToUpdate);

        // Assert
        assertFalse(result, "update should return false when the PcCase does not exist");
        verify(pcCaseRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenPcCaseIsDeleted() {
        // Arrange
        String id = "1";
        when(pcCaseRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = pcCaseService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the PcCase exists and is deleted");
        verify(pcCaseRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenPcCaseDoesNotExist() {
        // Arrange
        String id = "1";
        when(pcCaseRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = pcCaseService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the PcCase does not exist");
        verify(pcCaseRepository).existsById(id);
        verifyNoMoreInteractions(pcCaseRepository);
    }
}
