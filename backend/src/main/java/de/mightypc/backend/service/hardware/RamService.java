package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.repository.hardware.RamRepository;
import org.springframework.stereotype.Service;

@Service
public class RamService extends BaseService<RAM, String, RamRepository> {
    protected RamService(RamRepository ramRepository) {
        super(ramRepository);
    }

    @Override
    protected String getId(RAM entity) {
        return entity.hardwareSpec().id();
    }
}
