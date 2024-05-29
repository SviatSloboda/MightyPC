package de.mightypc.backend.service.hardware;

import de.mightypc.backend.service.hardware.BaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public abstract class BaseServiceTest<T, S extends BaseService<T, R, E>, R extends MongoRepository<T, String>, E extends NoSuchElementException> {
    protected R repository;
    protected S service;

    @BeforeEach
    void setup() {
        repository = getMockRepository();
        service = getService(repository);
    }

    @Test
    void getAllEntities_shouldReturnAllEntities() {
        // Arrange
        List<T> expected = new ArrayList<>(List.of(getEntity()));
        when(repository.findAll()).thenReturn(expected);

        // Act
        List<T> actual = service.getAll();

        // Assert
        verify(repository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getAllEntities_shouldThrowHardwareNotFoundException_whenNoEntitiesWereFound() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act & Assert
        assertThrows(getException().getClass(), () -> service.getAll());
        verify(repository).findAll();
    }

    @Test
    void getById_shouldReturnEntity() {
        // Arrange
        T entity = getEntity();
        when(repository.findById("testId")).thenReturn(Optional.of(entity));

        // Act
        T actual = service.getById("testId");

        // Assert
        verify(repository).findById("testId");
        assertEquals(entity, actual);
    }

    @Test
    void getById_shouldThrowHardwareNotFoundException_whenEntityWasNotFound() {
        // Arrange
        when(repository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(getException().getClass(), () -> service.getById("testId"));
        verify(repository).findById("testId");
    }

    @Test
    void save_shouldSaveEntityAndReturnThisEntity() {
        // Arrange
        T entity = getEntity();
        when(repository.save(entity)).thenReturn(entity);

        // Act
        T actual = service.save(entity);

        // Assert
        verify(repository).save(entity);
        assertEquals(entity, actual);
    }

    @Test
    void deleteById_shouldDeleteAndReturnTrue() {
        // Arrange
        when(repository.existsById("testId")).thenReturn(true).thenReturn(false);

        // Act
        boolean actual = service.deleteById("testId");

        // Assert
        verify(repository, times(2)).existsById("testId");
        verify(repository).deleteById("testId");
        assertTrue(actual);
    }

    @Test
    void deleteById_shouldThrowHardwareNotFoundException_whenEntityDoesNotExist() {
        // Arrange
        when(repository.existsById("testId")).thenReturn(false);

        // Act & Assert
        assertThrows(getException().getClass(),
                () -> service.deleteById("testId"),
                "Entity was not Found. Id of entity: testId");
        verify(repository).existsById("testId");
    }

    @Test
    void getAllByPage_shouldReturnPageWithEntities() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 1);
        T entity = getEntity();
        Page<T> expectedPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<T> actualPage = service.getAllByPage(pageable);

        // Assert
        assertEquals(expectedPage, actualPage);
    }

    protected abstract E getException();

    protected abstract R getMockRepository();

    protected abstract S getService(R repository);

    protected abstract T getEntity();

    abstract void update_shouldUpdateEntityAndReturnIt();

    abstract void update_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository();

    abstract void attachPhoto_shouldAttachPhotoCorrectly();

    abstract void attachPhoto_shouldThrowHardwareNotFoundException_whenEntityDoesNotExistInRepository();
}
