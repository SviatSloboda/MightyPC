package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.repository.hardware.RamRepository;
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

class RamServiceTest {
    private final RamRepository ramRepository = mock(RamRepository.class);
    private final RamService ramService = new RamService(ramRepository);

    @Test
    void getAll_ReturnsAllRams() {
        // Arrange
        List<RAM> expected = new ArrayList<>(List.of(new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f)));
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

        // Act
        List<RAM> actual = ramService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no RAMs exist");
        verify(ramRepository).findAll();
    }

    @Test
    void getById_ReturnsRamWhenExists() {
        //Arrange
        Optional<RAM> expected = Optional.of(new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f));
        when(ramRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<RAM> actual = ramService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the RAM when it exists");
        verify(ramRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenRamDoesNotExist() {
        //Arrange
        Optional<RAM> expected = Optional.empty();
        when(ramRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<RAM> actual = ramService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the RAM does not exist");
        verify(ramRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenRamIsPersisted() {
        // Arrange
        RAM ramToSave = new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f);
        when(ramRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = ramService.save(ramToSave);

        // Assert
        assertTrue(result, "save should return true when the RAM is persisted");
        verify(ramRepository).save(ramToSave);
        verify(ramRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenRamIsNotPersisted() {
        // Arrange
        RAM ramToSave = new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f);
        when(ramRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = ramService.save(ramToSave);

        // Assert
        assertFalse(result, "save should return false when the RAM is not persisted");
        verify(ramRepository).save(ramToSave);
        verify(ramRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenRamExists() {
        // Arrange
        RAM ramToUpdate = new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f);
        when(ramRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = ramService.update(ramToUpdate);

        // Assert
        assertTrue(result, "update should return true when the RAM exists and is updated");
        verify(ramRepository).save(ramToUpdate);
        verify(ramRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenRamDoesNotExist() {
        // Arrange
        RAM ramToUpdate = new RAM("1", "test", "test", "type", 1, 1, 1.01f, 1.01f);
        when(ramRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = ramService.update(ramToUpdate);

        // Assert
        assertFalse(result, "update should return false when the RAM does not exist");
        verify(ramRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenRamIsDeleted() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = ramService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the RAM exists and is deleted");
        verify(ramRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenRamDoesNotExist() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = ramService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the RAM does not exist");
        verify(ramRepository).existsById(id);
        verifyNoMoreInteractions(ramRepository);
    }
}

