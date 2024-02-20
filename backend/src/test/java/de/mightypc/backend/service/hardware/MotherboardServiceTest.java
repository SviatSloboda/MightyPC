package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.HardwareNotFoundException;
import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

class MotherboardServiceTest {
    private final MotherboardRepository motherboardRepository = mock(MotherboardRepository.class);
    private final MotherboardService motherboardService = new MotherboardService(motherboardRepository);

    private static Motherboard getMotherboard() {
        return new Motherboard("1", new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                9500,
                new String[]{},
                new String[]{}
        );
    }

    @Test
    void getAll_ReturnsAllMotherboards() {
        // Arrange
        List<Motherboard> expected = new ArrayList<>(List.of(getMotherboard()));
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

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, motherboardService::getAll);
    }

    @Test
    void getById_ReturnsMotherboardWhenExists() {
        //Arrange
        Motherboard expected = getMotherboard();
        when(motherboardRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        Motherboard actual = motherboardService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the Motherboard when it exists");
        verify(motherboardRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenMotherboardDoesNotExist() {
        //Arrange
        when(motherboardRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> motherboardService.getById("1"),
                "getById should throw HardwareNotFoundException when the Motherboard does not exist");
        verify(motherboardRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedMotherboard() {
        // Arrange
        Motherboard expected = getMotherboard();
        when(motherboardRepository.save(expected)).thenReturn(expected);

        // Act
        Motherboard actual = motherboardService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved Motherboard");
        verify(motherboardRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedMotherboardWhenMotherboardExists() {
        // Arrange
        Motherboard expected = getMotherboard();
        when(motherboardRepository.existsById("1")).thenReturn(true);
        when(motherboardRepository.save(expected)).thenReturn(expected);

        // Act
        Motherboard actual = motherboardService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated Motherboard");
        verify(motherboardRepository).save(expected);
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenMotherboardDoesNotExist() {
        // Arrange
        Motherboard expected = getMotherboard();
        when(motherboardRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> motherboardService.update(expected),
                "update should throw HardwareNotFoundException when the Motherboard does not exist");
        verify(motherboardRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesMotherboardWhenExists() {
        // Arrange
        String id = "1";
        when(motherboardRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = motherboardService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the Motherboard exists and is deleted");
        verify(motherboardRepository).deleteById(id);
        verify(motherboardRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenMotherboardDoesNotExist() {
        // Arrange
        String id = "1";
        when(motherboardRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> motherboardService.deleteById(id), "Expected HardwareNotFoundException when Motherboard does not exist");
        verify(motherboardRepository).existsById(id);
    }

    @Test
    void attachPhoto_WhenMotherboardExists_ThenPhotoIsAttached() {
        // Arrange
        String motherboardId = "1";
        String photoUrl = "https://example.com/photo.jpg";
        Motherboard motherboardBeforeUpdate = new Motherboard("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), 9500, new String[]{}, new String[]{}, new ArrayList<>());
        Optional<Motherboard> optionalMotherboardBeforeUpdate = Optional.of(motherboardBeforeUpdate);

        List<String> photosWithNewUrl = new ArrayList<>();
        photosWithNewUrl.add(photoUrl);
        Motherboard motherboardAfterUpdate = new Motherboard("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), 9500, new String[]{}, new String[]{}, photosWithNewUrl);

        when(motherboardRepository.findById(motherboardId)).thenReturn(optionalMotherboardBeforeUpdate);
        when(motherboardRepository.save(any(Motherboard.class))).thenReturn(motherboardAfterUpdate);

        // Act
        motherboardService.attachPhoto(motherboardId, photoUrl);

        // Assert
        verify(motherboardRepository).findById(motherboardId);
        verify(motherboardRepository).save(motherboardAfterUpdate);
    }

    @Test
    void attachPhoto_WhenMotherboardDoesNotExist_ThenNoActionTaken() {
        // Arrange
        String motherboardId = "nonExistingId";
        String photoUrl = "https://example.com/photo.jpg";
        when(motherboardRepository.findById(motherboardId)).thenReturn(Optional.empty());

        // Act
        motherboardService.attachPhoto(motherboardId, photoUrl);

        // Assert
        verify(motherboardRepository).findById(motherboardId);
    }
}
