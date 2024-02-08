package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.springframework.stereotype.Service;

@Service
public class GpuService extends BaseService<GPU, String, GpuRepository> {
    protected GpuService(GpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(GPU entity) {
        return entity.hardwareSpec().id();
    }
}