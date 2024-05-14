package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.hardware.SSD;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SsdRepository extends MongoRepository<SSD, String> {
}
