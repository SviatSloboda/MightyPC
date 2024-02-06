package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.repository.PcComponent;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PowerSupplyService implements PcComponent<PowerSupply> {
    private final PowerSupplyRepository powerSupplyRepository;

    public PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        this.powerSupplyRepository = powerSupplyRepository;
    }

    @Override
    public List<PowerSupply> getAll() {
        return powerSupplyRepository.findAll();
    }

    @Override
    public Optional<PowerSupply> getById(String id) {
        return powerSupplyRepository.findById(id);
    }

    @Override
    public boolean save(PowerSupply obj) {
        powerSupplyRepository.save(obj);

        return powerSupplyRepository.existsById(obj.id());
    }

    @Override
    public boolean update(PowerSupply obj) {
        if (powerSupplyRepository.existsById(obj.id())) {
            powerSupplyRepository.save(obj);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteById(String id) {
        if (powerSupplyRepository.existsById(id)) {
            powerSupplyRepository.deleteById(id);
            return !powerSupplyRepository.existsById(id);
        }

        return false;
    }
}
