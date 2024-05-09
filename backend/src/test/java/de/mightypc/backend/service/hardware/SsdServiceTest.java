package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.SsdNotFoundException;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class SsdServiceTest extends BaseServiceTest<SSD, SsdService, SsdRepository, SsdNotFoundException> {
    private final SsdRepository mockSsdRepository = mock(SsdRepository.class);
    private final SsdService ssdService = new SsdService(mockSsdRepository);

    private final SSD testSsd = new SSD(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            23
    );

    private final SSD testSsd2 = new SSD(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 4.89f),
            2342,
            222
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<SSD> ssds = new ArrayList<>(List.of(testSsd, testSsd2));

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
        assertThrows(SsdNotFoundException.class, () -> service.update(expected));
        verify(mockSsdRepository).existsById("testId");
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
        // Arrange
        SSD expected = testSsd.withPhotos(List.of("Test"));

        when(mockSsdRepository.findById("testId")).thenReturn(Optional.of(testSsd));
        when(mockSsdRepository.save(testSsd.withSsdPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        SSD actualSsd = ssdService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualSsd.ssdPhotos(), expected.ssdPhotos());
        verify(mockSsdRepository).findById("testId");
        verify(mockSsdRepository).save(testSsd.withSsdPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockSsdRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SsdNotFoundException.class, () -> ssdService.attachPhoto("testId", "TEST"));
        verify(mockSsdRepository).findById("testId");
    }

    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testSsd);

        // Assert
        assertEquals(testSsd.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllSsdsWithProperSorting() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(List.of(testSsd, testSsd2), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllSsdsWithProperSorting() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(List.of(testSsd2, testSsd), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllSsdsWithProperSorting() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(List.of(testSsd2, testSsd), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllSsdsWithProperSorting() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(List.of(testSsd, testSsd2), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllSsdsWithProperFiltering() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(Collections.singletonList(testSsd2), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllSsdsWithProperFiltering() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(Collections.singletonList(testSsd), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByCapacityAsPages_shouldGetAllSsdsWithProperFiltering() {
        // Arrange
        Page<SSD> expected = new PageImpl<>(Collections.singletonList(testSsd), pageable, 8);
        when(repository.findAll()).thenReturn(ssds);

        // Act
        Page<SSD> actual = service.getAllWithFilteringByCapacityAsPages(pageable, 1, 50);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    protected SsdNotFoundException getException() {
        return new SsdNotFoundException("there is no such ssd!");
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
