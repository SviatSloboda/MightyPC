package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.hardware.GPU;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GpuRepository extends MongoRepository<GPU, String> {
}
