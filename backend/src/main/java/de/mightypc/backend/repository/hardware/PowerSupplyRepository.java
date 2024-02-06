package de.mightypc.backend.repository.hardware;

import de.mightypc.backend.model.specs.PowerSupply;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PowerSupplyRepository extends MongoRepository<PowerSupply, String> {
}
