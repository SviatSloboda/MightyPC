package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PowerSupplyNotFoundException;
import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
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

class PowerSupplyServiceTest extends BaseServiceTest<PowerSupply, PowerSupplyService, PowerSupplyRepository, PowerSupplyNotFoundException> {
    private final PowerSupplyRepository mockPowerSupplyRepository = mock(PowerSupplyRepository.class);
    private final PowerSupplyService powerSupplyService = new PowerSupplyService(mockPowerSupplyRepository);

    private final PowerSupply testPowerSupply = new PowerSupply(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            123
    );

    private final PowerSupply testPowerSupply2 = new PowerSupply(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 3.9f),
            1230
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<PowerSupply> powerSupplies = new ArrayList<>(List.of(testPowerSupply, testPowerSupply2));

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        PowerSupply expected = testPowerSupply.withPower(999);

        when(mockPowerSupplyRepository.existsById("testId")).thenReturn(true);
        when(mockPowerSupplyRepository.save(expected)).thenReturn(expected);

        // Act
        PowerSupply actual = service.update(expected);

        // Assert
        verify(mockPowerSupplyRepository).save(actual);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        PowerSupply expected = testPowerSupply.withPower(999);
        when(mockPowerSupplyRepository.existsById("testId")).thenReturn(false);
        when(mockPowerSupplyRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(PowerSupplyNotFoundException.class, () -> service.update(expected));
        verify(mockPowerSupplyRepository).existsById("testId");
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockPowerSupplyRepository.findAll()).thenReturn(List.of(testPowerSupply));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        // Arrange
        PowerSupply expected = testPowerSupply.withPhotos(List.of("Test"));

        when(mockPowerSupplyRepository.findById("testId")).thenReturn(Optional.of(testPowerSupply));
        when(mockPowerSupplyRepository.save(testPowerSupply.withPowerSupplyPhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        PowerSupply actualPowerSupply = powerSupplyService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualPowerSupply.powerSupplyPhotos(), expected.powerSupplyPhotos());
        verify(mockPowerSupplyRepository).findById("testId");
        verify(mockPowerSupplyRepository).save(testPowerSupply.withPowerSupplyPhotos(new ArrayList<>(List.of("Test"))));
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockPowerSupplyRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PowerSupplyNotFoundException.class, () -> powerSupplyService.attachPhoto("testId", "TEST"));
        verify(mockPowerSupplyRepository).findById("testId");
    }

    @Test
    void getAllPowerSuppliesByEnergyConsumption_shouldReturnNamesAndPricesOfFilteredPowerSupplies() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockPowerSupplyRepository.findAll()).thenReturn(List.of(testPowerSupply));

        // Act
        Map<String, String> actual = powerSupplyService.getAllPowerSuppliesByEnergyConsumption(100);

        // Assert
        verify(mockPowerSupplyRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllPowerSuppliesByEnergyConsumption_shouldReturnEmptyHashMap_when_entitiesWithSpecifiedEnergyConsumptionWereFound() {
        // Arrange
        when(mockPowerSupplyRepository.findAll()).thenReturn(List.of(testPowerSupply));

        // Act
        Map<String, String> actual = powerSupplyService.getAllPowerSuppliesByEnergyConsumption(1000);

        // Assert
        verify(mockPowerSupplyRepository).findAll();
        assertEquals(new HashMap<>(), actual);
    }

    @Test
    void getAllPowerSuppliesByEnergyConsumption_shouldThrowHardwareNotFoundExceptionWhenNoEntitiesWereFound() {
        // Arrange
        when(mockPowerSupplyRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(PowerSupplyNotFoundException.class,
                () -> powerSupplyService.getAllPowerSuppliesByEnergyConsumption(1000));

        verify(mockPowerSupplyRepository).findAll();
    }


    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testPowerSupply);

        // Assert
        assertEquals(testPowerSupply.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllPowerSuppliesWithProperSorting() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(List.of(testPowerSupply, testPowerSupply2), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllPowerSuppliesWithProperSorting() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(List.of(testPowerSupply2, testPowerSupply), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllPowerSuppliesWithProperSorting() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(List.of(testPowerSupply, testPowerSupply2), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllPowerSuppliesWithProperSorting() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(List.of(testPowerSupply2, testPowerSupply), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPowerAsPages_shouldGetAllPowerSuppliesWithProperFiltering() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(Collections.singletonList(testPowerSupply), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithFilteringByPowerAsPages(pageable, 100, 300);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllPowerSuppliesWithProperFiltering() {
        // Arrange
        Page<PowerSupply> expected = new PageImpl<>(Collections.singletonList(testPowerSupply), pageable, 8);
        when(repository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    protected PowerSupplyNotFoundException getException() {
        return new PowerSupplyNotFoundException("there is no such psu!");
    }

    @Override
    protected PowerSupplyRepository getMockRepository() {
        return mockPowerSupplyRepository;
    }

    @Override
    protected PowerSupplyService getService(PowerSupplyRepository repository) {
        return new PowerSupplyService(repository);
    }

    @Override
    protected PowerSupply getEntity() {
        return testPowerSupply;
    }
}
