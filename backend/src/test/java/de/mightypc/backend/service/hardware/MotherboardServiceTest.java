package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.MotherboardNotFoundException;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class MotherboardServiceTest extends BaseServiceTest<Motherboard, MotherboardService, MotherboardRepository, MotherboardNotFoundException> {
    private final MotherboardRepository mockMotherboardRepository = mock(MotherboardRepository.class);
    private final MotherboardService motherboardService = new MotherboardService(mockMotherboardRepository);

    private final Motherboard testMotherboard = new Motherboard(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            23,
            "afd4"
    );

    private final Motherboard testMotherboard2 = new Motherboard(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 4.99f),
            230,
            "lol"
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<Motherboard> motherboards = new ArrayList<>(List.of(testMotherboard, testMotherboard2));

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
        assertThrows(MotherboardNotFoundException.class, () -> service.update(expected));
        verify(mockMotherboardRepository).existsById("testId");
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

        assertThrows(MotherboardNotFoundException.class, () -> motherboardService.attachPhoto("testId", "TEST"));
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


    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testMotherboard);

        // Assert
        assertEquals(testMotherboard.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllMotherboardsWithProperSorting() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(List.of(testMotherboard, testMotherboard2), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllMotherboardsWithProperSorting() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(List.of(testMotherboard2, testMotherboard), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllMotherboardsWithProperSorting() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(List.of(testMotherboard2, testMotherboard), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllMotherboardsWithProperSorting() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(List.of(testMotherboard, testMotherboard2), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByEnergyConsumptionAsPages_shouldGetAllMotherboardsWithProperFiltering() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(Collections.singletonList(testMotherboard2), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithFilteringByEnergyConsumptionAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllMotherboardsWithProperFiltering() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(Collections.singletonList(testMotherboard), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringBySocketAsPages_shouldGetAllMotherboardsWithProperFiltering() {
        // Arrange
        Page<Motherboard> expected = new PageImpl<>(Collections.singletonList(testMotherboard), pageable, 8);
        when(repository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = service.getAllWithFilteringBySocketAsPages(pageable, "afd4");

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    protected MotherboardNotFoundException getException() {
        return new MotherboardNotFoundException("no mam");
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
