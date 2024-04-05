package de.mightypc.backend.repository.pc.hardware;

import de.mightypc.backend.model.pc.specs.Motherboard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MotherboardRepository extends MongoRepository<Motherboard, String> {
    public List<Motherboard> getAllBySocket(String socket);
}
