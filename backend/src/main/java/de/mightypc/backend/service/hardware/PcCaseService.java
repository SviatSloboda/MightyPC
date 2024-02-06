package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.repository.PcComponent;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PcCaseService implements PcComponent<PcCase> {
    private final PcCaseRepository pcCaseRepository;

    public PcCaseService(PcCaseRepository pcCaseRepository) {
        this.pcCaseRepository = pcCaseRepository;
    }

    @Override
    public List<PcCase> getAll() {
        return pcCaseRepository.findAll();
    }

    @Override
    public Optional<PcCase> getById(String id) {
        return pcCaseRepository.findById(id);
    }

    @Override
    public boolean save(PcCase obj) {
        pcCaseRepository.save(obj);

        return pcCaseRepository.existsById(obj.id());
    }

    @Override
    public boolean update(PcCase obj) {
        if (pcCaseRepository.existsById(obj.id())) {
            pcCaseRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (pcCaseRepository.existsById(id)) {
            pcCaseRepository.deleteById(id);
            return !pcCaseRepository.existsById(id);
        }

        return false;
    }
}
