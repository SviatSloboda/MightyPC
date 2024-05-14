package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PcCaseNotFoundException;
import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
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

class PcCaseServiceTest extends BaseServiceTest<PcCase, PcCaseService, PcCaseRepository, PcCaseNotFoundException> {
    private final PcCaseRepository mockPcCaseRepository = mock(PcCaseRepository.class);
    private final PcCaseService pcCaseService = new PcCaseService(mockPcCaseRepository);

    private final PcCase testPcCase = new PcCase(
            "testId",
            new HardwareSpec("test", "test", new BigDecimal(666), 1.99f),
            "3x3x3"
    );

    private final PcCase testPcCase2 = new PcCase(
            "testId2",
            new HardwareSpec("test", "test", new BigDecimal(333), 4.39f),
            "3x3x3"
    );

    private final PageRequest pageable = PageRequest.of(0, 8);

    private final List<PcCase> pcCases = new ArrayList<>(List.of(testPcCase, testPcCase2));

    @Override
    @Test
    void update_shouldUpdateEntityAndReturnIt() {
        // Arrange
        PcCase expected = testPcCase.withDimensions("999");

        when(mockPcCaseRepository.existsById("testId")).thenReturn(true);
        when(mockPcCaseRepository.save(expected)).thenReturn(expected);

        // Act
        PcCase actual = service.update(expected);

        // Assert
        verify(mockPcCaseRepository).save(actual);
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        PcCase expected = testPcCase.withDimensions("999");
        when(mockPcCaseRepository.existsById("testId")).thenReturn(false);
        when(mockPcCaseRepository.save(expected)).thenReturn(expected);

        // Act && Assert
        assertThrows(PcCaseNotFoundException.class, () -> service.update(expected));
        verify(mockPcCaseRepository).existsById("testId");
    }

    @Override
    @Test
    void getAllNamesWithPrices_shouldReturnMapOfNamesWithPrices() {
        // Arrange
        HashMap<String, String> expected = new HashMap<>();
        expected.put("testId", "test ($666)");
        when(mockPcCaseRepository.findAll()).thenReturn(List.of(testPcCase));

        // Act
        HashMap<String, String> actual = service.getAllNamesWithPrices();

        // Assert
        assertEquals(expected, actual);
        verify(mockPcCaseRepository).findAll();
    }

    @Override
    @Test
    void attachPhoto_shouldAttachPhotoCorrectly() {
        // Arrange
        PcCase expected = testPcCase.withPhotos(List.of("Test"));

        when(mockPcCaseRepository.findById("testId")).thenReturn(Optional.of(testPcCase));
        when(mockPcCaseRepository.save(testPcCase.withPcCasePhotos(new ArrayList<>(List.of("Test"))))).thenReturn(expected);

        // Act
        PcCase actualPcCase = pcCaseService.attachPhoto("testId", "Test");

        // Assert
        assertEquals(actualPcCase.pcCasePhotos(), expected.pcCasePhotos());
        verify(mockPcCaseRepository).findById("testId");
        verify(mockPcCaseRepository).save(testPcCase.withPcCasePhotos(new ArrayList<>(List.of("Test"))));
    }


    @Test
    void getNameOfEntity_shouldReturnCorrectNameOfEntity() {
        // Arrange & Act
        String actual = service.getNameOfEntity(testPcCase);

        // Assert
        assertEquals(testPcCase.hardwareSpec().name(), actual);
    }

    @Test
    void getAllWithSortingOfPriceDescAsPages_shouldGetAllPcCasesWithProperSorting() {
        // Arrange
        Page<PcCase> expected = new PageImpl<>(List.of(testPcCase, testPcCase2), pageable, 8);
        when(repository.findAll()).thenReturn(pcCases);

        // Act
        Page<PcCase> actual = service.getAllWithSortingOfPriceDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfPriceAscAsPages_shouldGetAllPcCasesWithProperSorting() {
        // Arrange
        Page<PcCase> expected = new PageImpl<>(List.of(testPcCase2, testPcCase), pageable, 8);
        when(repository.findAll()).thenReturn(pcCases);

        // Act
        Page<PcCase> actual = service.getAllWithSortingOfPriceAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingDescAsPages_shouldGetAllPcCasesWithProperSorting() {
        // Arrange
        Page<PcCase> expected = new PageImpl<>(List.of(testPcCase2, testPcCase), pageable, 8);
        when(repository.findAll()).thenReturn(pcCases);

        // Act
        Page<PcCase> actual = service.getAllWithSortingOfRatingDescAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithSortingOfRatingAscAsPages_shouldGetAllPcCasesWithProperSorting() {
        // Arrange
        Page<PcCase> expected = new PageImpl<>(List.of(testPcCase, testPcCase2), pageable, 8);
        when(repository.findAll()).thenReturn(pcCases);

        // Act
        Page<PcCase> actual = service.getAllWithSortingOfRatingAscAsPages(pageable);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllWithFilteringByPriceAsPages_shouldGetAllPcCasesWithProperFiltering() {
        // Arrange
        Page<PcCase> expected = new PageImpl<>(Collections.singletonList(testPcCase), pageable, 8);
        when(repository.findAll()).thenReturn(pcCases);

        // Act
        Page<PcCase> actual = service.getAllWithFilteringByPriceAsPages(pageable, 500, 2500);

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Override
    @Test
    void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository() {
        // Arrange
        when(mockPcCaseRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PcCaseNotFoundException.class, () -> pcCaseService.attachPhoto("testId", "TEST"));
        verify(mockPcCaseRepository).findById("testId");
    }

    @Override
    protected PcCaseNotFoundException getException() {
        return new PcCaseNotFoundException("there is no such pcCase!");
    }

    @Override
    protected PcCaseRepository getMockRepository() {
        return mockPcCaseRepository;
    }

    @Override
    protected PcCaseService getService(PcCaseRepository repository) {
        return new PcCaseService(repository);
    }

    @Override
    protected PcCase getEntity() {
        return testPcCase;
    }
}
