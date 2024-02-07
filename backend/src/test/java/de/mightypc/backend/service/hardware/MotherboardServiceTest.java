package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
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

class MotherboardServiceTest {
    private final MotherboardRepository motherboardRepository = mock(MotherboardRepository.class);
    private final MotherboardService motherboardService = new MotherboardService(motherboardRepository);

    @Test
    void getAll_ReturnsAllMotherboards() {
        // Arrange
        List<Motherboard> expected = new ArrayList<>(List.of(new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f)));
        when(motherboardRepository.findAll()).thenReturn(expected);

        // Act
        List<Motherboard> actual = motherboardService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all Motherboards");
        verify(motherboardRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoMotherboards() {
        // Arrange
        List<Motherboard> expected = new ArrayList<>();
        when(motherboardRepository.findAll()).thenReturn(expected);

        // Act
        List<Motherboard> actual = motherboardService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no Motherboards exist");
        verify(motherboardRepository).findAll();
    }

    @Test
    void getById_ReturnsMotherboardWhenExists() {
        //Arrange
        Optional<Motherboard> expected = Optional.of(new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f));
        when(motherboardRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<Motherboard> actual = motherboardService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the Motherboard when it exists");
        verify(motherboardRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenMotherboardDoesNotExist() {
        //Arrange
        Optional<Motherboard> expected = Optional.empty();
        when(motherboardRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<Motherboard> actual = motherboardService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the Motherboard does not exist");
        verify(motherboardRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenMotherboardIsPersisted() {
        // Arrange
        Motherboard motherboardToSave = new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f);
        when(motherboardRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = motherboardService.save(motherboardToSave);

        // Assert
        assertTrue(result, "save should return true when the Motherboard is persisted");
        verify(motherboardRepository).save(motherboardToSave);
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenMotherboardIsNotPersisted() {
        // Arrange
        Motherboard motherboardToSave = new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f);
        when(motherboardRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = motherboardService.save(motherboardToSave);

        // Assert
        assertFalse(result, "save should return false when the Motherboard is not persisted");
        verify(motherboardRepository).save(motherboardToSave);
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenMotherboardExists() {
        // Arrange
        Motherboard motherboardToUpdate = new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f);
        when(motherboardRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = motherboardService.update(motherboardToUpdate);

        // Assert
        assertTrue(result, "update should return true when the Motherboard exists and is updated");
        verify(motherboardRepository).save(motherboardToUpdate);
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenMotherboardDoesNotExist() {
        // Arrange
        Motherboard motherboardToUpdate = new Motherboard("1", "test", "test", 1.01f, 1, new GPU[]{}, new CPU[]{}, 1.01f);
        when(motherboardRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = motherboardService.update(motherboardToUpdate);

        // Assert
        assertFalse(result, "update should return false when the Motherboard does not exist");
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenMotherboardIsDeleted() {
        // Arrange
        String id = "1";
        when(motherboardRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = motherboardService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the Motherboard exists and is deleted");
        verify(motherboardRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenMotherboardDoesNotExist() {
        // Arrange
        String id = "1";
        when(motherboardRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = motherboardService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the Motherboard does not exist");
        verify(motherboardRepository).existsById(id);
        verifyNoMoreInteractions(motherboardRepository);
    }
}
