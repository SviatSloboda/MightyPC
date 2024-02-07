package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;
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

class HddServiceTest {
    private final HddRepository hddRepository = mock(HddRepository.class);
    private final HddService hddService = new HddService(hddRepository);

    @Test
    void getAll_ReturnsAllHdds() {
        // Arrange
        List<HDD> expected = new ArrayList<>(List.of(new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f)));
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

        // Act
        List<HDD> actual = hddService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no HDDs exist");
        verify(hddRepository).findAll();
    }

    @Test
    void getById_ReturnsHddWhenExists() {
        //Arrange
        Optional<HDD> expected = Optional.of(new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f));
        when(hddRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<HDD> actual = hddService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the HDD when it exists");
        verify(hddRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenHddDoesNotExist() {
        //Arrange
        Optional<HDD> expected = Optional.empty();
        when(hddRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<HDD> actual = hddService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the HDD does not exist");
        verify(hddRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenHddIsPersisted() {
        // Arrange
        HDD hddToSave = new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f);
        when(hddRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = hddService.save(hddToSave);

        // Assert
        assertTrue(result, "save should return true when the HDD is persisted");
        verify(hddRepository).save(hddToSave);
        verify(hddRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenHddIsNotPersisted() {
        // Arrange
        HDD hddToSave = new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f);
        when(hddRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = hddService.save(hddToSave);

        // Assert
        assertFalse(result, "save should return false when the HDD is not persisted");
        verify(hddRepository).save(hddToSave);
        verify(hddRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenHddExists() {
        // Arrange
        HDD hddToUpdate = new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f);
        when(hddRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = hddService.update(hddToUpdate);

        // Assert
        assertTrue(result, "update should return true when the HDD exists and is updated");
        verify(hddRepository).save(hddToUpdate);
        verify(hddRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenHddDoesNotExist() {
        // Arrange
        HDD hddToUpdate = new HDD("1", "test", "test", 1, 1, 1.01f, 1.01f);
        when(hddRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = hddService.update(hddToUpdate);

        // Assert
        assertFalse(result, "update should return false when the HDD does not exist");
        verify(hddRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenHddIsDeleted() {
        // Arrange
        String id = "1";
        when(hddRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = hddService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the HDD exists and is deleted");
        verify(hddRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenHddDoesNotExist() {
        // Arrange
        String id = "1";
        when(hddRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = hddService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the HDD does not exist");
        verify(hddRepository).existsById(id);
        verifyNoMoreInteractions(hddRepository);
    }
}

