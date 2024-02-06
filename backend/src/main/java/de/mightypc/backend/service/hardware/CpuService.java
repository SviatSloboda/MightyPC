package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.PcComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CpuService implements PcComponent<CPU> {
    private final CpuRepository cpuRepository;

    public CpuService(CpuRepository cpuRepository) {
        this.cpuRepository = cpuRepository;
    }

    @Override
    public List<CPU> getAll() {
        return cpuRepository.findAll();
    }

    @Override
    public Optional<CPU> getById(String id) {
        return cpuRepository.findById(id);
    }

    @Override
    public boolean save(CPU obj) {
        cpuRepository.save(obj);

        return cpuRepository.existsById(obj.id());
    }

    @Override
    public boolean update(CPU obj) {
        if (cpuRepository.existsById(obj.id())) {
            cpuRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (cpuRepository.existsById(id)) {
            cpuRepository.deleteById(id);
            return !cpuRepository.existsById(id);
        }

        return false;
    }
}