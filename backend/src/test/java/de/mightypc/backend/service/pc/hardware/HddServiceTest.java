package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.service.hardware.HddService;
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

class HddServiceTest extends BaseServiceTest<HDD, HddService, HddRepository> {
    private final HddRepository mockHddRepository = mock(HddRepository.class);
    private final HDD testHdd = new HDD(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            23
    );

    private final HddService hddService = new HddService(mockHddRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        HDD expected = testHdd.withEnergyConsumption(999);

        when(mockHddRepository.existsById("testId")).thenReturn(true);
        when(mockHddRepository.save(expected)).thenReturn(expected);

        // Act
        HDD actual = service.update(expected);

        // Assert
        verify(mockHddRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        HDD expected = testHdd.withEnergyConsumption(999);
        when(mockHddRepository.existsById("testId")).thenReturn(false);
        when(mockHddRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockHddRepository).existsById("testId");
        verifyNoMoreInteractions(mockHddRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockHddRepository.findAll()).thenReturn(List.of(testHdd));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockHddRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        HDD expected = testHdd.withPhotos(List.of("Test"));

        when(mockHddRepository.findById("testId")).thenReturn(Optional.of(testHdd));
        when(mockHddRepository.save(testHdd.withHddPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        HDD actualHdd = hddService.attachPhoto("testId", "Test");

        assertEquals(actualHdd.hddPhotos(), expected.hddPhotos());
        verify(mockHddRepository).findById("testId");
        verify(mockHddRepository).save(testHdd.withHddPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockHddRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> hddService.attachPhoto("testId", "TEST"));
        verify(mockHddRepository).findById("testId");
    }

    @Override
    protected HddRepository getMockRepository() {
        return mockHddRepository;
    }

    @Override
    protected HddService getService(HddRepository repository) {
        return new HddService(repository);
    }

    @Override
    protected HDD getEntity() {
        return testHdd;
    }
}
