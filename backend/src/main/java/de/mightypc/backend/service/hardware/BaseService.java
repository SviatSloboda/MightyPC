package de.mightypc.backend.service.hardware;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class BaseService<T, R extends MongoRepository<T, String>, E extends NoSuchElementException> {
    protected R repository;
    protected E exception;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    public List<T> getAll() {
        List<T> entities = repository.findAll();

        if (entities.isEmpty()) throw getException("No entities were retrieved");

        return entities;
    }

    public T getById(String id) {
        return repository.findById(id).orElseThrow(() -> getException("There is no such entity with id: " + id));
    }

    @Transactional
    public T update(T entity) {
        String entityId = getId(entity);

        if (!repository.existsById(entityId)) {
            throw getException((getNotFoundMessage(entityId)));
        }

        return repository.save(entity);
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public boolean deleteById(String id) {
        if (!repository.existsById(id)) {
            throw getException((getNotFoundMessage(id)));
        }

        repository.deleteById(id);

        return !repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Page<T> getAllByPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    private String getNotFoundMessage(String id) {
        return "Entity: " + getNameOfEntity(getById(id)) + " was not found!!! Id of entity: " + id;
    }

    protected abstract E getException(String message);

    public abstract String getAllNamesWithPrices();

    public abstract T attachPhoto(String id, String photoUrl);

    protected abstract String getId(T entity);

    protected abstract String getNameOfEntity(T entity);
}
