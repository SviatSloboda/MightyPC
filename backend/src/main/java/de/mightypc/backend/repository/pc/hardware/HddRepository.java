package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.HDD;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HddRepository extends MongoRepository<HDD, String> {
}
