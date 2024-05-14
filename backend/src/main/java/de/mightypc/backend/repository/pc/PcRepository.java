package de.mightypc.backend.repository.pc;

import de.mightypc.backend.model.pc.PC;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PcRepository extends MongoRepository<PC, String> {
}
