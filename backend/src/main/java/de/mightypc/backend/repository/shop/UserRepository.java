package de.mightypc.backend.repository.shop;

import de.mightypc.backend.model.shop.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Boolean existsByEmail(String email);

    User getUserByEmail(String email);
}