package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SsdService extends BaseService<SSD, String, SsdRepository> {
    protected SsdService(SsdRepository ssdRepository){
        super(ssdRepository);
    }
    @Override
    protected String getId(SSD entity) {
        return entity.hardwareSpec().id();
    }
}
