package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.stereotype.Service;

@Service
public class PowerSupplyService extends BaseService<PowerSupply, PowerSupplyRepository> {
    protected PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        super(powerSupplyRepository);
    }

    @Override
    protected String getId(PowerSupply entity) {
        return entity.id();
    }
}
