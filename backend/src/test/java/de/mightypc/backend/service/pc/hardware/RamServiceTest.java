package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.service.hardware.RamService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class RamServiceTest extends BaseServiceTest<RAM, RamService, RamRepository> {
    private final RamRepository mockRamRepository = mock(RamRepository.class);
    private final RAM testRam = new RAM(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            "DDR4",
            23,
            23
    );

    private final RamService ramService = new RamService(mockRamRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        RAM expected = testRam.withEnergyConsumption(999);

        when(mockRamRepository.existsById("testId")).thenReturn(true);
        when(mockRamRepository.save(expected)).thenReturn(expected);

        // Act
        RAM actual = service.update(expected);

        // Assert
        verify(mockRamRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        RAM expected = testRam.withEnergyConsumption(999);
        when(mockRamRepository.existsById("testId")).thenReturn(false);
        when(mockRamRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockRamRepository).existsById("testId");
        verifyNoMoreInteractions(mockRamRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockRamRepository.findAll()).thenReturn(List.of(testRam));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockRamRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        RAM expected = testRam.withPhotos(List.of("Test"));

        when(mockRamRepository.findById("testId")).thenReturn(Optional.of(testRam));
        when(mockRamRepository.save(testRam.withRamPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        RAM actualRam = ramService.attachPhoto("testId", "Test");

        assertEquals(actualRam.ramPhotos(), expected.ramPhotos());
        verify(mockRamRepository).findById("testId");
        verify(mockRamRepository).save(testRam.withRamPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockRamRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> ramService.attachPhoto("testId", "TEST"));
        verify(mockRamRepository).findById("testId");
    }

    @Override
    protected RamRepository getMockRepository() {
        return mockRamRepository;
    }

    @Override
    protected RamService getService(RamRepository repository) {
        return new RamService(repository);
    }

    @Override
    protected RAM getEntity() {
        return testRam;
    }
}
