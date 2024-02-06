package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.repository.PcComponent;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SsdService implements PcComponent<SSD> {
    private final SsdRepository ssdRepository;

    public SsdService(SsdRepository ssdRepository) {
        this.ssdRepository = ssdRepository;
    }

    @Override
    public List<SSD> getAll() {
        return ssdRepository.findAll();
    }

    @Override
    public Optional<SSD> getById(String id) {
        return ssdRepository.findById(id);
    }

    @Override
    public boolean save(SSD obj) {
        ssdRepository.save(obj);

        return ssdRepository.existsById(obj.id());
    }

    @Override
    public boolean update(SSD obj) {
        if (ssdRepository.existsById(obj.id())) {
            ssdRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (ssdRepository.existsById(id)) {
            ssdRepository.deleteById(id);
            return !ssdRepository.existsById(id);
        }

        return false;
    }
}
