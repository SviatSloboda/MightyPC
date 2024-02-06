package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.specs.CPU;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CpuRepository extends MongoRepository<CPU, String> {

}
