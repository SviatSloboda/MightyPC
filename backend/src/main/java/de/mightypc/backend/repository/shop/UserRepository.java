package de.mightypc.backend.repository.shop;

import de.mightypc.backend.model.shop.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Boolean existsByEmail(String email);

    User getUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
