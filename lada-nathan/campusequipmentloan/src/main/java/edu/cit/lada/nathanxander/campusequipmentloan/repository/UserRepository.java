package edu.cit.lada.nathanxander.campusequipmentloan.repository;

import edu.cit.lada.nathanxander.campusequipmentloan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
