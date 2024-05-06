package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.RamNotFoundException;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
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

class RamServiceTest extends BaseServiceTest<RAM, RamService, RamRepository, RamNotFoundException> {
    private final RamRepository mockRamRepository = mock(RamRepository.class);
    private final RamService ramService = new RamService(mockRamRepository);

    private final RAM testRam = new RAM(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            "DDR4",
            23,
            23
    );

    private final RAM testRam2 = new RAM(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 4.99f),
            "DDR228",
            239,
            230
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<RAM> rams = new ArrayList<>(List.of(testRam, testRam2));

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
        assertThrows(RamNotFoundException.class, () -> service.update(expected));
        verify(mockRamRepository).existsById("testId");
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
        // Arrange
        RAM expected = testRam.withPhotos(List.of("Test"));

        when(mockRamRepository.findById("testId")).thenReturn(Optional.of(testRam));
        when(mockRamRepository.save(testRam.withRamPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        RAM actualRam = ramService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualRam.ramPhotos(), expected.ramPhotos());
        verify(mockRamRepository).findById("testId");
        verify(mockRamRepository).save(testRam.withRamPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockRamRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RamNotFoundException.class, () -> ramService.attachPhoto("testId", "TEST"));
        verify(mockRamRepository).findById("testId");
    }

    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testRam);

        // Assert
        assertEquals(testRam.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllRamsWithProperSorting() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(List.of(testRam, testRam2), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllRamsWithProperSorting() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(List.of(testRam2, testRam), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllRamsWithProperSorting() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(List.of(testRam, testRam2), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllRamsWithProperSorting() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(List.of(testRam2, testRam), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllRamsWithProperFiltering() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(Collections.singletonList(testRam2), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllRamsWithProperFiltering() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(Collections.singletonList(testRam), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByMemorySizeAsPages_shouldGetAllRamsWithProperFiltering() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(Collections.singletonList(testRam), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithFilteringByMemorySizeAsPages(pageable, 1, 50);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByTypeSizeAsPages_shouldGetAllRamsWithProperFiltering() {
        // Arrange
        Page<RAM> expected = new PageImpl<>(Collections.singletonList(testRam2), pageable, 8);
        when(repository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = service.getAllWithFilteringByTypeAsPages(pageable, "DDR228");

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    protected RamNotFoundException getException() {
        return new RamNotFoundException("there is no such ram!");
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
