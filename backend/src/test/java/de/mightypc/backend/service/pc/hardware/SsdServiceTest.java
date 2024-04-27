package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.service.hardware.SsdService;
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

class SsdServiceTest extends BaseServiceTest<SSD, SsdService, SsdRepository> {
    private final SsdRepository mockSsdRepository = mock(SsdRepository.class);
    private final SSD testSsd = new SSD(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            23
    );

    private final SsdService ssdService = new SsdService(mockSsdRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        SSD expected = testSsd.withEnergyConsumption(999);

        when(mockSsdRepository.existsById("testId")).thenReturn(true);
        when(mockSsdRepository.save(expected)).thenReturn(expected);

        // Act
        SSD actual = service.update(expected);

        // Assert
        verify(mockSsdRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        SSD expected = testSsd.withEnergyConsumption(999);
        when(mockSsdRepository.existsById("testId")).thenReturn(false);
        when(mockSsdRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockSsdRepository).existsById("testId");
        verifyNoMoreInteractions(mockSsdRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockSsdRepository.findAll()).thenReturn(List.of(testSsd));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockSsdRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        SSD expected = testSsd.withPhotos(List.of("Test"));

        when(mockSsdRepository.findById("testId")).thenReturn(Optional.of(testSsd));
        when(mockSsdRepository.save(testSsd.withSsdPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        SSD actualSsd = ssdService.attachPhoto("testId", "Test");

        assertEquals(actualSsd.ssdPhotos(), expected.ssdPhotos());
        verify(mockSsdRepository).findById("testId");
        verify(mockSsdRepository).save(testSsd.withSsdPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockSsdRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> ssdService.attachPhoto("testId", "TEST"));
        verify(mockSsdRepository).findById("testId");
    }

    @Override
    protected SsdRepository getMockRepository() {
        return mockSsdRepository;
    }

    @Override
    protected SsdService getService(SsdRepository repository) {
        return new SsdService(repository);
    }

    @Override
    protected SSD getEntity() {
        return testSsd;
    }
}
