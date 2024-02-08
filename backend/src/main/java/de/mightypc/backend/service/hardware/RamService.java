package de.mightypc.backend.service.hardware;

import com.fasterxml.jackson.databind.ser.Serializers;
import de.mightypc.backend.model.specs.RAM;
import de.mightypc.backend.repository.hardware.RamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
