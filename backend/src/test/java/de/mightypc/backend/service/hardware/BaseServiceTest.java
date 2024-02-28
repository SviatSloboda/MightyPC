package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.service.pc.hardware.BaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

class BaseServiceTest {

    @Mock
    private MongoRepository<Object, String> repository;

    private BaseService<Object, MongoRepository<Object, String>> baseService;

    @BeforeEach
    void setUp() {
        try {
            MockitoAnnotations.openMocks(this);
            baseService = new BaseService<>(repository) {
                @Override
                protected String getId(Object entity) {
                    return "1";
                }
            };
        } catch (MockitoException e) {
            e.getUnfilteredStackTrace();
        }
    }

    @Test
    void getAll_ReturnsAllEntities() {
        // Arrange
        List<Object> expected = new ArrayList<>(List.of(new Object()));
        when(repository.findAll()).thenReturn(expected);

        // Act
        List<Object> actual = baseService.getAll();

        // Assert
        assertEquals(expected, actual, "getAll should return all entities");
        verify(repository).findAll();
    }


    @Test
    void getAll_ReturnsEmptyListWhenNoEntities() {
        // Arrange
        List<Object> expected = new ArrayList<>();
        when(repository.findAll()).thenReturn(expected);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, baseService::getAll);
    }

    @Test
    void getById_ReturnsEntityWhenExists() {
        //Arrange
        Object expected = new Object();
        when(repository.findById("1")).thenReturn(Optional.of(expected));

        //Act
        Object actual = baseService.getById("1");

        //Assert
        assertEquals(expected, actual, "getById should return the entity when it exists");
        verify(repository).findById("1");
    }

    @Test
    void getById_ThrowsExceptionWhenEntityDoesNotExist() {
        //Arrange
        when(repository.findById("1")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> baseService.getById("1"),
                "getById should throw HardwareNotFoundException when the entity does not exist");
        verify(repository).findById("1");
    }

    @Test
    void save_ReturnsSavedEntity() {
        // Arrange
        Object expected = new Object();
        when(repository.save(expected)).thenReturn(expected);

        // Act
        Object actual = baseService.save(expected);

        // Assert
        assertEquals(expected, actual, "save should return the saved entity");
        verify(repository).save(expected);
    }

    @Test
    void update_ReturnsUpdatedEntityWhenEntityExists() {
        // Arrange
        Object expected = new Object();
        when(repository.existsById("1")).thenReturn(true);
        when(repository.save(expected)).thenReturn(expected);

        // Act
        Object actual = baseService.update(expected);

        // Assert
        assertEquals(expected, actual, "update should return the updated entity");
        verify(repository).save(expected);
        verify(repository).existsById("1");
    }

    @Test
    void update_ThrowsExceptionWhenEntityDoesNotExist() {
        // Arrange
        Object expected = new Object();
        when(repository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> baseService.update(expected),
                "update should throw HardwareNotFoundException when the entity does not exist");
        verify(repository).existsById("1");
    }

    @Test
    void deleteById_DeletesEntityWhenExists() {
        // Arrange
        String id = "1";
        when(repository.existsById(id)).thenReturn(true).thenReturn(false);

        // Act
        boolean result = baseService.deleteById(id);

        // Assert
        assertTrue(result, "deleteById should return true when the entity exists and is deleted");
        verify(repository).deleteById(id);
        verify(repository, times(2)).existsById(id);
    }

    @Test
    void deleteById_ThrowsHardwareNotFoundExceptionWhenEntityDoesNotExist() {
        // Arrange
        String id = "1";
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(HardwareNotFoundException.class, () -> baseService.deleteById(id), "Expected HardwareNotFoundException when entity does not exist");
        verify(repository).existsById(id);
    }
}
