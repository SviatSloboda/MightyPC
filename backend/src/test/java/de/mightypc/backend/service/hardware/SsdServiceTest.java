package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
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

class SsdServiceTest {
    private final SsdRepository ssdRepository = mock(SsdRepository.class);
    private final SsdService ssdService = new SsdService(ssdRepository);

    @Test
    void getAll_ReturnsAllSsds() {
        // Arrange
        List<SSD> expected = new ArrayList<>(List.of(new SSD("1", "test", "test", 1, 1.01f, 1.01f)));
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

        // Act
        List<SSD> actual = ssdService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no SSDs exist");
        verify(ssdRepository).findAll();
    }

    @Test
    void getById_ReturnsSsdWhenExists() {
        //Arrange
        Optional<SSD> expected = Optional.of(new SSD("1", "test", "test", 1, 1.01f, 1.01f));
        when(ssdRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<SSD> actual = ssdService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the SSD when it exists");
        verify(ssdRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenSsdDoesNotExist() {
        //Arrange
        Optional<SSD> expected = Optional.empty();
        when(ssdRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<SSD> actual = ssdService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the SSD does not exist");
        verify(ssdRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenSsdIsPersisted() {
        // Arrange
        SSD ssdToSave = new SSD("1", "test", "test", 1, 1.01f, 1.01f);
        when(ssdRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = ssdService.save(ssdToSave);

        // Assert
        assertTrue(result, "save should return true when the SSD is persisted");
        verify(ssdRepository).save(ssdToSave);
        verify(ssdRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenSsdIsNotPersisted() {
        // Arrange
        SSD ssdToSave = new SSD("1", "test", "test", 1, 1.01f, 1.01f);
        when(ssdRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = ssdService.save(ssdToSave);

        // Assert
        assertFalse(result, "save should return false when the SSD is not persisted");
        verify(ssdRepository).save(ssdToSave);
        verify(ssdRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenSsdExists() {
        // Arrange
        SSD ssdToUpdate = new SSD("1", "test", "test", 1, 1.01f, 1.01f);
        when(ssdRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = ssdService.update(ssdToUpdate);

        // Assert
        assertTrue(result, "update should return true when the SSD exists and is updated");
        verify(ssdRepository).save(ssdToUpdate);
        verify(ssdRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenSsdDoesNotExist() {
        // Arrange
        SSD ssdToUpdate = new SSD("1", "test", "test", 1, 1.01f, 1.01f);
        when(ssdRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = ssdService.update(ssdToUpdate);

        // Assert
        assertFalse(result, "update should return false when the SSD does not exist");
        verify(ssdRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenSsdIsDeleted() {
        // Arrange
        String id = "1";
        when(ssdRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = ssdService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the SSD exists and is deleted");
        verify(ssdRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenSsdDoesNotExist() {
        // Arrange
        String id = "1";
        when(ssdRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = ssdService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the SSD does not exist");
        verify(ssdRepository).existsById(id);
        verifyNoMoreInteractions(ssdRepository);
    }
}
