package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.RAM;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RamRepository extends MongoRepository<RAM, String> {
}
