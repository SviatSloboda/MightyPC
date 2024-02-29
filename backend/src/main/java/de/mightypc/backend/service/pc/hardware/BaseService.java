package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseService<T, R extends MongoRepository<T, String>> {
    protected R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<T> getAll() {
        List<T> entities = repository.findAll();

        if (entities.isEmpty()) throw new HardwareNotFoundException("No entities were retrieved");

        return entities;
    }

    @Transactional(readOnly = true)
    public T getById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new HardwareNotFoundException((getNotFoundMessage(id))));
    }

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public T update(T entity) {
        String entityId = getId(entity);
        if (!repository.existsById(entityId)) {
            throw new HardwareNotFoundException((getNotFoundMessage(entityId)));
        }

        return repository.save(entity);
    }

    @Transactional
    public boolean deleteById(String id) {
        if (!repository.existsById(id)) {
            throw new HardwareNotFoundException((getNotFoundMessage(id)));
        }

        repository.deleteById(id);

        return !repository.existsById(id);
    }

    public Page<T> getAllByPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    private String getNotFoundMessage(String id) {
        return "Entity was not Found. Id of entity: " + id;
    }

    protected abstract String getId(T entity);

    public Map<String, String> getAllNames() {
        return new HashMap<>(Collections.emptyMap());
    }
}