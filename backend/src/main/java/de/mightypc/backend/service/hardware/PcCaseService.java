package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.springframework.stereotype.Service;

@Service
public class PcCaseService extends BaseService<PcCase, String, PcCaseRepository> {
    protected PcCaseService(PcCaseRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(PcCase entity) {
        return entity.hardwareSpec().id();
    }
}