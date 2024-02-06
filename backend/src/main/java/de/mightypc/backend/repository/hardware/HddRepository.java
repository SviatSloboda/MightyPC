package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.specs.HDD;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HddRepository extends MongoRepository<HDD, String> {
}
