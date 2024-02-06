package de.mightypc.backend.repository;

import java.util.List;
import java.util.Optional;

public interface PcComponent <T>{
    public List<T> getAll();
    public Optional<T> getById(String id);
    public boolean save(T obj);
    public boolean update(T obj);
    public boolean deleteById(String id);
}
