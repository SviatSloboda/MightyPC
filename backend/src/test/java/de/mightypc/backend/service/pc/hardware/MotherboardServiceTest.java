package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MotherboardServiceTest extends BaseServiceTest<Motherboard, MotherboardService, MotherboardRepository> {
    private final MotherboardRepository mockMotherboardRepository = mock(MotherboardRepository.class);
    private final Motherboard testMotherboard = new Motherboard(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            "afd4"
    );

    private final MotherboardService motherboardService = new MotherboardService(mockMotherboardRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        Motherboard expected = testMotherboard.withEnergyConsumption(999);

        when(mockMotherboardRepository.existsById("testId")).thenReturn(true);
        when(mockMotherboardRepository.save(expected)).thenReturn(expected);

        // Act
        Motherboard actual = service.update(expected);

        // Assert
        verify(mockMotherboardRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        Motherboard expected = testMotherboard.withEnergyConsumption(999);
        when(mockMotherboardRepository.existsById("testId")).thenReturn(false);
        when(mockMotherboardRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockMotherboardRepository).existsById("testId");
        verifyNoMoreInteractions(mockMotherboardRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockMotherboardRepository.findAll()).thenReturn(List.of(testMotherboard));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        Motherboard expected = testMotherboard.withPhotos(List.of("Test"));

        when(mockMotherboardRepository.findById("testId")).thenReturn(Optional.of(testMotherboard));
        when(mockMotherboardRepository.save(testMotherboard.withMotherboardPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        Motherboard actualMotherboard = motherboardService.attachPhoto("testId", "Test");

        assertEquals(actualMotherboard.motherboardPhotos(), expected.motherboardPhotos());
        verify(mockMotherboardRepository).findById("testId");
        verify(mockMotherboardRepository).save(testMotherboard.withMotherboardPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockMotherboardRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> motherboardService.attachPhoto("testId", "TEST"));
        verify(mockMotherboardRepository).findById("testId");
    }

    @Test
    void getMotherboardsBySocket_shouldReturnAllMotherboardsWithSpecifiedSocket() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");

        when(mockMotherboardRepository.findAll()).thenReturn(List.of(testMotherboard));

        // Act
        Map<String, String> actual = motherboardService.getMotherboardsBySocket("afd4");

        // Assert
        verify(mockMotherboardRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getMotherboardsBySocket_shouldReturnEmptyHashmap_when_noMotherboardsWithSpecifiedSocketWereFound() {
        // Arrange
        when(mockMotherboardRepository.findAll()).thenReturn(List.of(testMotherboard));

        // Act
        Map<String, String> actual = motherboardService.getMotherboardsBySocket("notExisting");

        // Assert
        verify(mockMotherboardRepository).findAll();
        assertEquals(new HashMap<>(), actual);
    }

    @Override
    protected MotherboardRepository getMockRepository() {
        return mockMotherboardRepository;
    }

    @Override
    protected MotherboardService getService(MotherboardRepository repository) {
        return new MotherboardService(repository);
    }

    @Override
    protected Motherboard getEntity() {
        return testMotherboard;
    }
}
