package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.service.hardware.PcCaseService;
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

class PcCaseServiceTest extends BaseServiceTest<PcCase, PcCaseService, PcCaseRepository> {
    private final PcCaseRepository mockPcCaseRepository = mock(PcCaseRepository.class);
    private final PcCase testPcCase = new PcCase(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            "3x3x3"
    );

    private final PcCaseService pcCaseService = new PcCaseService(mockPcCaseRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        PcCase expected = testPcCase.withDimensions("999");

        when(mockPcCaseRepository.existsById("testId")).thenReturn(true);
        when(mockPcCaseRepository.save(expected)).thenReturn(expected);

        // Act
        PcCase actual = service.update(expected);

        // Assert
        verify(mockPcCaseRepository).save(actual);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        PcCase expected = testPcCase.withDimensions("999");
        when(mockPcCaseRepository.existsById("testId")).thenReturn(false);
        when(mockPcCaseRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(HardwareNotFoundException.class, () -> service.update(expected));
        verify(mockPcCaseRepository).existsById("testId");
        verifyNoMoreInteractions(mockPcCaseRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockPcCaseRepository.findAll()).thenReturn(List.of(testPcCase));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockPcCaseRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        PcCase expected = testPcCase.withPhotos(List.of("Test"));

        when(mockPcCaseRepository.findById("testId")).thenReturn(Optional.of(testPcCase));
        when(mockPcCaseRepository.save(testPcCase.withPcCasePhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        PcCase actualPcCase = pcCaseService.attachPhoto("testId", "Test");

        assertEquals(actualPcCase.pcCasePhotos(), expected.pcCasePhotos());
        verify(mockPcCaseRepository).findById("testId");
        verify(mockPcCaseRepository).save(testPcCase.withPcCasePhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockPcCaseRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HardwareNotFoundException.class, () -> pcCaseService.attachPhoto("testId", "TEST"));
        verify(mockPcCaseRepository).findById("testId");
    }

    @Override
    protected PcCaseRepository getMockRepository() {
        return mockPcCaseRepository;
    }

    @Override
    protected PcCaseService getService(PcCaseRepository repository) {
        return new PcCaseService(repository);
    }

    @Override
    protected PcCase getEntity() {
        return testPcCase;
    }
}
