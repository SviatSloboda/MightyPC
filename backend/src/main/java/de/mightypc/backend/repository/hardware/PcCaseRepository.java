package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.pc.specs.PcCase;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PcCaseRepository extends MongoRepository<PcCase, String> {
}
