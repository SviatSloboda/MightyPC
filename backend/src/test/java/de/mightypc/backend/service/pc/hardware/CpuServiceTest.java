package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.exception.pc.hardware.CpuNotFoundException;
import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.service.hardware.CpuService;
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

class CpuServiceTest extends BaseServiceTest<CPU, CpuService, CpuRepository, CpuNotFoundException> {
    private final CpuRepository mockCpuRepository = mock(CpuRepository.class);
    private final CPU testCpu = new CPU(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            "testSocket"
    );

    private final CpuService cpuService = new CpuService(mockCpuRepository);

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        CPU expected = testCpu.withEnergyConsumption(999);

        when(mockCpuRepository.existsById("testId")).thenReturn(true);
        when(mockCpuRepository.save(expected)).thenReturn(expected);

        // Act
        CPU actual = service.update(expected);

        // Assert
        verify(mockCpuRepository).save(expected);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        CPU expected = testCpu.withEnergyConsumption(999);
        when(mockCpuRepository.existsById("testId")).thenReturn(false);
        when(mockCpuRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        verify(mockCpuRepository).existsById("testId");
        assertThrows(CpuNotFoundException.class, () -> service.update(expected));
        verifyNoMoreInteractions(mockCpuRepository);
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockCpuRepository.findAll()).thenReturn(List.of(testCpu));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockCpuRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        CPU expected = testCpu.withPhotos(List.of("Test"));

        when(mockCpuRepository.findById("testId")).thenReturn(Optional.of(testCpu));
        when(mockCpuRepository.save(testCpu.withCpuPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        CPU actualCpu = cpuService.attachPhoto("testId", "Test");

        assertEquals(actualCpu.cpuPhotos(), expected.cpuPhotos());
        verify(mockCpuRepository).findById("testId");
        verify(mockCpuRepository).save(testCpu.withCpuPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CpuNotFoundException.class, () -> cpuService.attachPhoto("testId", "TEST"));
        verify(mockCpuRepository).findById("testId");
    }

    @Test
    void getSocketOfCpuById_shouldGiveCorrectSocket() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.of(testCpu));

        // Act
        String actual = cpuService.getSocketOfCpuById("testId");

        // Assert
        verify(mockCpuRepository).findById("testId");
        assertEquals(testCpu.socket(), actual);
    }

    @Test
    void getSocketOfCpuById_shouldThrowHardwareNotFoundException_whenCpuDoesNotExistInRepository() {
        // Arrange
        when(mockCpuRepository.findById("testId")).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(CpuNotFoundException.class, () -> cpuService.getSocketOfCpuById("testId"));
        verify(mockCpuRepository).findById("testId");
    }

    @Override
    protected CpuNotFoundException getException() {
        return new CpuNotFoundException("There is no such Cpu with id: ");
    }

    @Override
    protected CpuRepository getMockRepository() {
        return mockCpuRepository;
    }

    @Override
    protected CpuService getService(CpuRepository repository) {
        return new CpuService(repository);
    }

    @Override
    protected CPU getEntity() {
        return testCpu;
    }
}
