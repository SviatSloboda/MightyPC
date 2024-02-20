package de.mightypc.backend.controller.pc.hardware;

import de.mightypc.backend.service.hardware.BaseService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping("/page")
    public Page<T> getAllByPage(Pageable pageable) {
        return service.getAllByPage(pageable);
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable String id) {
        return service.deleteById(id);
    }

    @PutMapping
    public T update(@RequestBody T entity) {
        return service.update(entity);
    }
}