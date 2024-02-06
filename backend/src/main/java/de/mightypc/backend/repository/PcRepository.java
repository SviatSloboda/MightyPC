package de.mightypc.backend.repository;

import de.mightypc.backend.model.PC;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PcRepository extends MongoRepository<PC, String> {
}
