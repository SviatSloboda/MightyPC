package de.mightypc.backend.repository.pc;

import de.mightypc.backend.model.pc.Workstation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkstationRepository extends MongoRepository<Workstation, String> {
}
