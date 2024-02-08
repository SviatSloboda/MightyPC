package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.service.hardware.BaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


import java.util.List;

public abstract class BaseController<T, ID, S extends BaseService<T, ID, ?>> {
    S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable ID id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable ID id) {
        return service.deleteById(id);
    }

    @PutMapping
    public T update(T entity) {
        return service.save(entity);
    }
}
