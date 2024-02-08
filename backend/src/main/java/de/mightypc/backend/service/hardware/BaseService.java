package de.mightypc.backend.service.hardware;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class BaseService<T, ID, R extends MongoRepository<T, ID>> {
    protected R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<T> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public T getById(ID id) {
        return repository.findById(id).orElseThrow(
                () -> new NoSuchElementException(getNotFoundMessage(id)));
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public T update(T entity) {
        if (!repository.existsById(getId(entity))) {
            throw new NoSuchElementException(getNotFoundMessage(getId(entity)));
        }

        return repository.save(entity);
    }

    @Transactional
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(getNotFoundMessage(id));
        }

        repository.deleteById(id);
    }

    private String getNotFoundMessage(ID id) {
        return "Entity was not Found. Id of entity: " + id;
    }

    protected abstract ID getId(T entity);
}