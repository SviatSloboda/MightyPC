package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.springframework.stereotype.Service;

@Service
public class MotherboardService extends BaseService<Motherboard, MotherboardRepository> {
    protected MotherboardService(MotherboardRepository motherboardRepository) {
        super(motherboardRepository);
    }

    @Override
    protected String getId(Motherboard entity) {
        return entity.id();
    }
}
