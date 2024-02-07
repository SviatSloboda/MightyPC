package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
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

class PowerSupplyServiceTest {
    private final PowerSupplyRepository powerSupplyRepository = mock(PowerSupplyRepository.class);
    private final PowerSupplyService powerSupplyService = new PowerSupplyService(powerSupplyRepository);

    @Test
    void getAll_ReturnsAllPowerSupplies() {
        // Arrange
        List<PowerSupply> expected = new ArrayList<>(List.of(new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f)));
        when(powerSupplyRepository.findAll()).thenReturn(expected);

        // Act
        List<PowerSupply> actual = powerSupplyService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all PowerSupplies");
        verify(powerSupplyRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoPowerSupplies() {
        // Arrange
        List<PowerSupply> expected = new ArrayList<>();
        when(powerSupplyRepository.findAll()).thenReturn(expected);

        // Act
        List<PowerSupply> actual = powerSupplyService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return an empty list when no PowerSupplies exist");
        verify(powerSupplyRepository).findAll();
    }

    @Test
    void getById_ReturnsPowerSupplyWhenExists() {
        //Arrange
        Optional<PowerSupply> expected = Optional.of(new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f));
        when(powerSupplyRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<PowerSupply> actual = powerSupplyService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the PowerSupply when it exists");
        verify(powerSupplyRepository).findById("1");
    }

    @Test
    void getById_ReturnsEmptyWhenPowerSupplyDoesNotExist() {
        //Arrange
        Optional<PowerSupply> expected = Optional.empty();
        when(powerSupplyRepository.findById("1")).thenReturn(expected);

        //Act
        Optional<PowerSupply> actual = powerSupplyService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return empty when the PowerSupply does not exist");
        verify(powerSupplyRepository).findById("1");
    }

    @Test
    void save_ReturnsTrueWhenPowerSupplyIsPersisted() {
        // Arrange
        PowerSupply powerSupplyToSave = new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f);
        when(powerSupplyRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = powerSupplyService.save(powerSupplyToSave);

        // Assert
        assertTrue(result, "save should return true when the PowerSupply is persisted");
        verify(powerSupplyRepository).save(powerSupplyToSave);
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void save_ReturnsFalseWhenPowerSupplyIsNotPersisted() {
        // Arrange
        PowerSupply powerSupplyToSave = new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f);
        when(powerSupplyRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = powerSupplyService.save(powerSupplyToSave);

        // Assert
        assertFalse(result, "save should return false when the PowerSupply is not persisted");
        verify(powerSupplyRepository).save(powerSupplyToSave);
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void update_ReturnsTrueWhenPowerSupplyExists() {
        // Arrange
        PowerSupply powerSupplyToUpdate = new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f);
        when(powerSupplyRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = powerSupplyService.update(powerSupplyToUpdate);

        // Assert
        assertTrue(result, "update should return true when the PowerSupply exists and is updated");
        verify(powerSupplyRepository).save(powerSupplyToUpdate);
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void update_ReturnsFalseWhenPowerSupplyDoesNotExist() {
        // Arrange
        PowerSupply powerSupplyToUpdate = new PowerSupply("1", "test", "test", 1, 1.01f, 1.01f);
        when(powerSupplyRepository.existsById("1")).thenReturn(false);

        // Act
        boolean result = powerSupplyService.update(powerSupplyToUpdate);

        // Assert
        assertFalse(result, "update should return false when the PowerSupply does not exist");
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void deleteById_ReturnsTrueWhenPowerSupplyIsDeleted() {
        // Arrange
        String id = "1";
        when(powerSupplyRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = powerSupplyService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the PowerSupply exists and is deleted");
        verify(powerSupplyRepository).deleteById(id);
    }

    @Test
    void deleteById_ReturnsFalseWhenPowerSupplyDoesNotExist() {
        // Arrange
        String id = "1";
        when(powerSupplyRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = powerSupplyService.deleteById(id);

        // Assert
        assertFalse(result, "deleteById should return false when the PowerSupply does not exist");
        verify(powerSupplyRepository).existsById(id);
        verifyNoMoreInteractions(powerSupplyRepository);
    }
}
