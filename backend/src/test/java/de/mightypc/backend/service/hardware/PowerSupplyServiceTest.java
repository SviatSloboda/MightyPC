package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PowerSupplyServiceTest {
    private final PowerSupplyRepository powerSupplyRepository = mock(PowerSupplyRepository.class);
    private final PowerSupplyService powerSupplyService = new PowerSupplyService(powerSupplyRepository);

    private static PowerSupply getPowerSupply() {
        return new PowerSupply("1", new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                1
        );
    }

    @Test
    void getAll_ReturnsAllPowerSupplys() {
        // Arrange
        List<PowerSupply> expected = new ArrayList<>(List.of(getPowerSupply()));
        when(powerSupplyRepository.findAll()).thenReturn(expected);

        // Act
        List<PowerSupply> actual = powerSupplyService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all PowerSupplys");
        verify(powerSupplyRepository).findAll();
    }

    @Test
    void getAll_ReturnsEmptyListWhenNoPowerSupplys() {
        // Arrange
        List<PowerSupply> expected = new ArrayList<>();
        when(powerSupplyRepository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, powerSupplyService::getAll);
    }

    @Test
    void getById_ReturnsPowerSupplyWhenExists() {
        //Arrange
        PowerSupply expected = getPowerSupply();
        when(powerSupplyRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        PowerSupply actual = powerSupplyService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the PowerSupply when it exists");
        verify(powerSupplyRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenPowerSupplyDoesNotExist() {
        //Arrange
        when(powerSupplyRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> powerSupplyService.getById("1"),
                "getById should throw HardwareNotFoundException when the PowerSupply does not exist");
        verify(powerSupplyRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedPowerSupply() {
        // Arrange
        PowerSupply expected = getPowerSupply();
        when(powerSupplyRepository.save(expected)).thenReturn(expected);

        // Act
        PowerSupply actual = powerSupplyService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved PowerSupply");
        verify(powerSupplyRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedPowerSupplyWhenPowerSupplyExists() {
        // Arrange
        PowerSupply expected = getPowerSupply();
        when(powerSupplyRepository.existsById("1")).thenReturn(true);
        when(powerSupplyRepository.save(expected)).thenReturn(expected);

        // Act
        PowerSupply actual = powerSupplyService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated PowerSupply");
        verify(powerSupplyRepository).save(expected);
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenPowerSupplyDoesNotExist() {
        // Arrange
        PowerSupply expected = getPowerSupply();
        when(powerSupplyRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> powerSupplyService.update(expected),
                "update should throw HardwareNotFoundException when the PowerSupply does not exist");
        verify(powerSupplyRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesPowerSupplyWhenExists() {
        // Arrange
        String id = "1";
        when(powerSupplyRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = powerSupplyService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the PowerSupply exists and is deleted");
        verify(powerSupplyRepository).deleteById(id);
        verify(powerSupplyRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenPowerSupplyDoesNotExist() {
        // Arrange
        String id = "1";
        when(powerSupplyRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> powerSupplyService.deleteById(id), "Expected HardwareNotFoundException when PowerSupply does not exist");
        verify(powerSupplyRepository).existsById(id);
    }
}
