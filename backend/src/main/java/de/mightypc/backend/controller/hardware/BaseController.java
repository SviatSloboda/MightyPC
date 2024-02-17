package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.service.hardware.BaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<T, S extends BaseService<T, ?>> {
    S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }

    @PutMapping
    public T update(@RequestBody T entity) {
        return service.save(entity);
    }
}