package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.pc.specs.PcCase;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.repository.pc.hardware.PcCaseRepository;
import de.mightypc.backend.service.pc.hardware.PcCaseService;
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

class PcCaseServiceTest {
    private final PcCaseRepository pcCaseRepository = mock(PcCaseRepository.class);
    private final PcCaseService pcCaseService = new PcCaseService(pcCaseRepository);

    private static PcCase getPcCase() {
        return new PcCase("1", new HardwareSpec(
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

    @Test
    void attachPhoto_WhenPcCaseExists_ThenPhotoIsAttached() {
        // Arrange
        String pcCaseId = "1";
        String photoUrl = "https://example.com/photo.jpg";
        PcCase pcCaseBeforeUpdate = new PcCase("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), "10x10x10", new ArrayList<>());
        Optional<PcCase> optionalPcCaseBeforeUpdate = Optional.of(pcCaseBeforeUpdate);

        List<String> photosWithNewUrl = new ArrayList<>();
        photosWithNewUrl.add(photoUrl);
        PcCase pcCaseAfterUpdate = new PcCase("1", new HardwareSpec("test", "test", new BigDecimal("1"), 1.01f), "10x10x10", photosWithNewUrl);

        when(pcCaseRepository.findById(pcCaseId)).thenReturn(optionalPcCaseBeforeUpdate);
        when(pcCaseRepository.save(any(PcCase.class))).thenReturn(pcCaseAfterUpdate);

        // Act
        pcCaseService.attachPhoto(pcCaseId, photoUrl);

        // Assert
        verify(pcCaseRepository).findById(pcCaseId);
        verify(pcCaseRepository).save(pcCaseAfterUpdate);
    }


    @Test
    void attachPhoto_WhenPcCaseDoesNotExist_ThenNoActionTaken() {
        // Arrange
        String pcCaseId = "nonExistingId";
        String photoUrl = "https://example.com/photo.jpg";
        when(pcCaseRepository.findById(pcCaseId)).thenReturn(Optional.empty());

        // Act
        pcCaseService.attachPhoto(pcCaseId, photoUrl);

        // Assert
        verify(pcCaseRepository).findById(pcCaseId);
    }
}
