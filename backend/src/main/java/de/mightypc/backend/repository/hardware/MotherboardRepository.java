package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.specs.Motherboard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MotherboardRepository extends MongoRepository<Motherboard, String> {
}
