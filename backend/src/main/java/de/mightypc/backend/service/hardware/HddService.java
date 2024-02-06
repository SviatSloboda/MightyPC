package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;

import de.mightypc.backend.repository.PcComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HddService implements PcComponent<HDD> {
    private final HddRepository hddRepository;

    public HddService(HddRepository hddRepository) {
        this.hddRepository = hddRepository;
    }

    @Override
    public List<HDD> getAll() {
        return hddRepository.findAll();
    }

    @Override
    public Optional<HDD> getById(String id) {
        return hddRepository.findById(id);
    }

    @Override
    public boolean save(HDD obj) {
        hddRepository.save(obj);

        return hddRepository.existsById(obj.id());
    }

    @Override
    public boolean update(HDD obj) {
        if (hddRepository.existsById(obj.id())) {
            hddRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (hddRepository.existsById(id)) {
            hddRepository.deleteById(id);
            return !hddRepository.existsById(id);
        }

        return false;
    }
}
