package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.springframework.stereotype.Service;

@Service
public class CpuService extends BaseService<CPU, String, CpuRepository>{
    protected CpuService(CpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(CPU entity) {
        return entity.hardwareSpec().id();
    }
}