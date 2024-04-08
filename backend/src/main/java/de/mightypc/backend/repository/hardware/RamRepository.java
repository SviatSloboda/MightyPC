package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.hardware.RAM;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RamRepository extends MongoRepository<RAM, String> {
}
