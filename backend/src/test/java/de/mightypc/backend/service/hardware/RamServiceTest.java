package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.RamNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
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
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockRamRepository.findAll()).thenReturn(rams);

        String expected = "$rams:\n{testId:test:($666)}\n{testId2:test:($333)}\n";

        // Act
        String actual = ramService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockRamRepository.findAll()).thenReturn(rams);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = ramService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockRamRepository.findAll()).thenReturn(rams);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "ram"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "ram")
        );

        // Act
        List<ItemForConfigurator> actual = ramService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getRams_shouldReturnFilteredAndSortedRams() {
        // Arrange
        List<RAM> expected = List.of(testRam);
        when(mockRamRepository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = ramService.getRams(pageable, "price-asc", 500, 700, 1, 50, "DDR4");

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getRams_shouldSortRamsByPriceDesc() {
        // Arrange
        List<RAM> expected = List.of(testRam, testRam2);
        when(mockRamRepository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = ramService.getRams(pageable, "price-desc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getRams_shouldSortRamsByRatingAsc() {
        // Arrange
        List<RAM> expected = List.of(testRam, testRam2);
        when(mockRamRepository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = ramService.getRams(pageable, "rating-asc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockRamRepository).findAll();
    }

    @Test
    void getRams_shouldSortRamsByRatingDesc() {
        // Arrange
        List<RAM> expected = List.of(testRam2, testRam);
        when(mockRamRepository.findAll()).thenReturn(rams);

        // Act
        Page<RAM> actual = ramService.getRams(pageable, "rating-desc", null, null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockRamRepository).findAll();
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
