package ru.phestrix.anonymouschat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.phestrix.anonymouschat.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
