package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.MotherboardNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        String expected = "$motherboards:\n{testId:test:($666)}\n{testId2:test:($333)}\n";

        // Act
        String actual = motherboardService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = motherboardService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "motherboard"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "motherboard")
        );

        // Act
        List<ItemForConfigurator> actual = motherboardService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getMotherboards_shouldSortMotherboardsByPriceDesc() {
        // Arrange
        List<Motherboard> expected = List.of(testMotherboard, testMotherboard2);
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = motherboardService.getMotherboards(pageable, "price-desc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getMotherboards_shouldSortMotherboardsByRatingAsc() {
        // Arrange
        List<Motherboard> expected = List.of(testMotherboard, testMotherboard2);
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = motherboardService.getMotherboards(pageable, "rating-asc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getMotherboards_shouldSortMotherboardsByRatingDesc() {
        // Arrange
        List<Motherboard> expected = List.of(testMotherboard2, testMotherboard);
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = motherboardService.getMotherboards(pageable, "rating-desc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockMotherboardRepository).findAll();
    }

    @Test
    void getMotherboards_shouldReturnFilteredAndSortedMotherboardsByEnergyConsumption() {
        // Arrange
        List<Motherboard> expected = List.of(testMotherboard2);
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = motherboardService.getMotherboards(pageable, "rating-desc", null, null, null, 200, 300);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockMotherboardRepository).findAll();
    }


    @Test
    void getMotherboards_shouldReturnFilteredAndSortedMotherboards() {
        // Arrange
        List<Motherboard> expected = List.of(testMotherboard);
        when(mockMotherboardRepository.findAll()).thenReturn(motherboards);

        // Act
        Page<Motherboard> actual = motherboardService.getMotherboards(pageable, "price-asc", 500, 700, "afd4", null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockMotherboardRepository).findAll();
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
