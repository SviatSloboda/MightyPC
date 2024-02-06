package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.PcComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotherboardService implements PcComponent<Motherboard> {
    private final MotherboardRepository motherboardRepository;

    public MotherboardService(MotherboardRepository motherboardRepository) {
        this.motherboardRepository = motherboardRepository;

    }

    @Override
    public List<Motherboard> getAll() {
        return motherboardRepository.findAll();
    }

    @Override
    public Optional<Motherboard> getById(String id) {
        return motherboardRepository.findById(id);
    }

    @Override
    public boolean save(Motherboard obj) {
        motherboardRepository.save(obj);

        return motherboardRepository.existsById(obj.id());
    }

    @Override
    public boolean update(Motherboard obj) {
        if (motherboardRepository.existsById(obj.id())) {
            motherboardRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (motherboardRepository.existsById(id)) {
            motherboardRepository.deleteById(id);
            return !motherboardRepository.existsById(id);
        }

        return false;
    }
}
