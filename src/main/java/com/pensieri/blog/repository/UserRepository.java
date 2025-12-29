package com.pensieri.blog.repository;
import com.pensieri.blog.model.User;
import com.pensieri.blog.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
}
