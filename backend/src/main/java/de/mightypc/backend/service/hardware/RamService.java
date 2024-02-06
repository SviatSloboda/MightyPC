package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.repository.PcComponent;
import de.mightypc.backend.repository.hardware.RamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RamService implements PcComponent<RAM> {
    private RamRepository ramRepository;

    public RamService(RamRepository ramRepository){
        this.ramRepository = ramRepository;
    }

    @Override
    public List<RAM> getAll() {
        return ramRepository.findAll();
    }

    @Override
    public Optional<RAM> getById(String id) {
        return ramRepository.findById(id);
    }

    @Override
    public boolean save(RAM obj) {
        ramRepository.save(obj);

        return ramRepository.existsById(obj.id());
    }

    @Override
    public boolean update(RAM obj) {
        if (ramRepository.existsById(obj.id())) {
            ramRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (ramRepository.existsById(id)) {
            ramRepository.deleteById(id);
            return !ramRepository.existsById(id);
        }

        return false;
    }
}
