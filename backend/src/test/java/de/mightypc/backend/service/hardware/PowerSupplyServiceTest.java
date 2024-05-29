package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PowerSupplyNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
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
    void getPowerSupplies_shouldSortPowerSuppliesByPriceDesc() {
        // Arrange
        List<PowerSupply> expected = List.of(testPowerSupply, testPowerSupply2);
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = powerSupplyService.getPowerSupplies(pageable, "price-desc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Test
    void getPowerSupplies_shouldSortPowerSuppliesByRatingAsc() {
        // Arrange
        List<PowerSupply> expected = List.of(testPowerSupply, testPowerSupply2);
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = powerSupplyService.getPowerSupplies(pageable, "rating-asc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Test
    void getPowerSupplies_shouldSortPowerSuppliesByRatingDesc() {
        // Arrange
        List<PowerSupply> expected = List.of(testPowerSupply2, testPowerSupply);
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = powerSupplyService.getPowerSupplies(pageable, "rating-desc", null, null, null, null);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 2), actual);
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
    void getAllNamesWithPrices_shouldReturnAllNamesWithPrices() {
        // Arrange
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        String expected = "power-supplies:\n{testId:test:($666)}\n{testId2:test:($333)}\n";

        // Act
        String actual = powerSupplyService.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Test
    void getAllIds_shouldReturnAllIds() {
        // Arrange
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        List<String> expected = List.of("testId", "testId2");

        // Act
        List<String> actual = powerSupplyService.getAllIds();

        // Assert
        assertEquals(expected, actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Test
    void getAllHardwareInfoForConfiguration_shouldReturnAllHardwareInfoForConfiguration() {
        // Arrange
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        List<ItemForConfigurator> expected = List.of(
                new ItemForConfigurator("testId", "test", new BigDecimal(666), "", "psu"),
                new ItemForConfigurator("testId2", "test", new BigDecimal(333), "", "psu")
        );

        // Act
        List<ItemForConfigurator> actual = powerSupplyService.getAllHardwareInfoForConfiguration();

        // Assert
        assertEquals(expected, actual);
        verify(mockPowerSupplyRepository).findAll();
    }

    @Test
    void getPowerSupplies_shouldReturnFilteredAndSortedPowerSupplies() {
        // Arrange
        List<PowerSupply> expected = List.of(testPowerSupply);
        when(mockPowerSupplyRepository.findAll()).thenReturn(powerSupplies);

        // Act
        Page<PowerSupply> actual = powerSupplyService.getPowerSupplies(pageable, "price-asc", 500, 700, 100, 300);

        // Assert
        assertEquals(new PageImpl<>(expected, pageable, 1), actual);
        verify(mockPowerSupplyRepository).findAll();
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
