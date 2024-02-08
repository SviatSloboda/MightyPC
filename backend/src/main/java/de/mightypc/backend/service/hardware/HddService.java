package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;

import org.springframework.stereotype.Service;

@Service
public class HddService extends BaseService<HDD, String, HddRepository> {
    protected HddService(HddRepository hddRepository){
        super(hddRepository);
    }

    @Override
    protected String getId(HDD entity) {
        return null;
    }
}
