package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.stereotype.Service;

@Service
public class SsdService extends BaseService<SSD, SsdRepository> {
    protected SsdService(SsdRepository ssdRepository) {
        super(ssdRepository);
    }

    @Override
    protected String getId(SSD entity) {
        return entity.id();
    }
}
