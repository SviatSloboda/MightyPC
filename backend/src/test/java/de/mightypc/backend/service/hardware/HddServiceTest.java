package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.HddNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockHddRepository.findAll()).thenReturn(hdds);

        String expected = "$hdds:\n{testId:test:($666)}\n{testId2:test:($333)}\n";

        // Act
        String actual = hddService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockHddRepository.findAll()).thenReturn(hdds);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = hddService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockHddRepository.findAll()).thenReturn(hdds);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "hdd"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "hdd")
        );

        // Act
        List<ItemForConfigurator> actual = hddService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getHdds_shouldReturnFilteredAndSortedHdds() {
        // Arrange
        List<HDD> expected = List.of(testHdd);
        when(mockHddRepository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = hddService.getHdds(pageable, "price-asc", 500, 700, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getHdds_shouldReturnFilteredAndSortedHddsByCapacity() {
        // Arrange
        List<HDD> expected = List.of(testHdd2);
        when(mockHddRepository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = hddService.getHdds(pageable, "price-asc", null, null, 200, 300, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getHdds_shouldReturnFilteredAndSortedHddsByEnergyConsumption() {
        // Arrange
        List<HDD> expected = List.of(testHdd2);
        when(mockHddRepository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = hddService.getHdds(pageable, "rating-desc", null, null, null, null, 200, 300);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getHdds_shouldSortHddsByPriceDesc() {
        // Arrange
        List<HDD> expected = List.of(testHdd, testHdd2);
        when(mockHddRepository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = hddService.getHdds(pageable, "price-desc", null, null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockHddRepository).findAll();
    }

    @Test
    void getHdds_shouldSortHddsByRatingAsc() {
        // Arrange
        List<HDD> expected = List.of(testHdd, testHdd2);
        when(mockHddRepository.findAll()).thenReturn(hdds);

        // Act
        Page<HDD> actual = hddService.getHdds(pageable, "rating-asc", null, null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockHddRepository).findAll();
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
