package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.PowerSupply;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PowerSupplyRepository extends MongoRepository<PowerSupply, String> {
}
