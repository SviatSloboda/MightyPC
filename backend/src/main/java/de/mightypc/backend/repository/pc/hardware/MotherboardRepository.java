package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.Motherboard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MotherboardRepository extends MongoRepository<Motherboard, String> {
}
