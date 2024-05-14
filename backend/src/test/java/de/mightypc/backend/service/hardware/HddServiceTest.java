package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.HddNotFoundException;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
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

class HddServiceTest extends BaseServiceTest<HDD, HddService, HddRepository, HddNotFoundException> {
    private final HddRepository mockHddRepository = mock(HddRepository.class);
    private final HddService hddService = new HddService(mockHddRepository);

    private final HDD testHdd = new HDD(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            23
    );

    private final HDD testHdd2 = new HDD(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 5.00f),
            250,
            299
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<HDD> hdds = new ArrayList<>(List.of(testHdd, testHdd2));

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
        assertThrows(HddNotFoundException.class, () -> service.update(expected));
        verify(mockHddRepository).existsById("testId");
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
        // Arrange
        HDD expected = testHdd.withPhotos(List.of("Test"));

        when(mockHddRepository.findById("testId")).thenReturn(Optional.of(testHdd));
        when(mockHddRepository.save(testHdd.withHddPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        HDD actualHdd = hddService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualHdd.hddPhotos(), expected.hddPhotos());
        verify(mockHddRepository).findById("testId");
        verify(mockHddRepository).save(testHdd.withHddPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        when(mockHddRepository.findById("testId")).thenReturn(Optional.empty());

        assertThrows(HddNotFoundException.class, () -> hddService.attachPhoto("testId", "TEST"));
        verify(mockHddRepository).findById("testId");
    }

    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testHdd);

        // Assert
        assertEquals(testHdd.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllHddsWithProperSorting() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(List.of(testHdd, testHdd2), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllHddsWithProperSorting() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(List.of(testHdd2, testHdd), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllHddsWithProperSorting() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(List.of(testHdd2, testHdd), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllHddsWithProperSorting() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(List.of(testHdd, testHdd2), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllHddsWithProperFiltering() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(Collections.singletonList(testHdd2), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllHddsWithProperFiltering() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(Collections.singletonList(testHdd), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByCapacityAsPages_shouldGetAllHddsWithProperFiltering() {
        // Arrange
        Page<HDD> expected = new PageImpl<>(Collections.singletonList(testHdd), pageable, 8);
        when(repository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = service.getAllWithFilteringByCapacityAsPages(pageable, 1, 50);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    protected HddNotFoundException getException() {
        return new HddNotFoundException("There is no such hdd with id: ");
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
