package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.PcComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GpuService implements PcComponent<GPU> {
    private final GpuRepository gpuRepository;

    public GpuService(GpuRepository gpuRepository) {
        this.gpuRepository = gpuRepository;
    }

    @Override
    public List<GPU> getAll() {
        return gpuRepository.findAll();
    }

    @Override
    public Optional<GPU> getById(String id) {
        return gpuRepository.findById(id);
    }

    @Override
    public boolean save(GPU obj) {
        gpuRepository.save(obj);

        return gpuRepository.existsById(obj.id());
    }

    @Override
    public boolean update(GPU obj) {
        if (gpuRepository.existsById(obj.id())) {
            gpuRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (gpuRepository.existsById(id)) {
            gpuRepository.deleteById(id);
            return !gpuRepository.existsById(id);
        }

        return false;
    }
}