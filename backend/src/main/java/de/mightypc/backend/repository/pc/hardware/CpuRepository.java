package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.CPU;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CpuRepository extends MongoRepository<CPU, String> {

}
