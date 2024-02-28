package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.pc.specs.RAM;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.repository.pc.hardware.RamRepository;
import de.mightypc.backend.service.pc.hardware.RamService;
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

class RamServiceTest {
    private final RamRepository ramRepository = mock(RamRepository.class);
    private final RamService ramService = new RamService(ramRepository);

    private static RAM getRam() {
        return new RAM("1", new HardwareSpec(
                "test",
                "test",
                new BigDecimal("1"),
                1.01f),
                "ddr2",
                123,
                125
        );
    }

    @Test
    void getAll_ReturnsAllRams() {
        // Arrange
        List<RAM> expected = new ArrayList<>(List.of(getRam()));
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

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, ramService::getAll);
    }

    @Test
    void getById_ReturnsRamWhenExists() {
        //Arrange
        RAM expected = getRam();
        when(ramRepository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        RAM actual = ramService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the RAM when it exists");
        verify(ramRepository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenRamDoesNotExist() {
        //Arrange
        when(ramRepository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.getById("1"),
                "getById should throw HardwareNotFoundException when the RAM does not exist");
        verify(ramRepository).findById("1");
    }

    @Test
    void save_ReturnsSavedRam() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.save(expected)).thenReturn(expected);

        // Act
        RAM actual = ramService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved RAM");
        verify(ramRepository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedRamWhenRamExists() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.existsById("1")).thenReturn(true);
        when(ramRepository.save(expected)).thenReturn(expected);

        // Act
        RAM actual = ramService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated RAM");
        verify(ramRepository).save(expected);
        verify(ramRepository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenRamDoesNotExist() {
        // Arrange
        RAM expected = getRam();
        when(ramRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.update(expected),
                "update should throw HardwareNotFoundException when the RAM does not exist");
        verify(ramRepository).existsById("1");
    }

    @Test
    void deleteById_DeletesRamWhenExists() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = ramService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the RAM exists and is deleted");
        verify(ramRepository).deleteById(id);
        verify(ramRepository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenRamDoesNotExist() {
        // Arrange
        String id = "1";
        when(ramRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> ramService.deleteById(id), "Expected HardwareNotFoundException when RAM does not exist");
        verify(ramRepository).existsById(id);
    }

    @Test
    void attachPhoto_WhenRamExists_ThenPhotoIsAttached() {
        // Arrange
        String ramId = "1";
        String photoUrl = "https://example.com/photo.jpg";
        RAM ramBeforeUpdate = new RAM("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), "ddr2", 123, 125, new ArrayList<>());
        Optional<RAM> optionalRamBeforeUpdate = Optional.of(ramBeforeUpdate);

        List<String> photosWithNewUrl = new ArrayList<>();
        photosWithNewUrl.add(photoUrl);
        RAM ramAfterUpdate = new RAM("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), "ddr2", 123, 125, photosWithNewUrl);

        when(ramRepository.findById(ramId)).thenReturn(optionalRamBeforeUpdate);
        when(ramRepository.save(any(RAM.class))).thenReturn(ramAfterUpdate);

        // Act
        ramService.attachPhoto(ramId, photoUrl);

        // Assert
        verify(ramRepository).findById(ramId);
        verify(ramRepository).save(ramAfterUpdate);
    }


    @Test
    void attachPhoto_WhenRamDoesNotExist_ThenNoActionTaken() {
        // Arrange
        String ramId = "nonExistingId";
        String photoUrl = "https://example.com/photo.jpg";
        when(ramRepository.findById(ramId)).thenReturn(Optional.empty());

        // Act
        ramService.attachPhoto(ramId, photoUrl);

        // Assert
        verify(ramRepository).findById(ramId);
    }
}
