package edu.uclm.esi.users.dao;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.users.model.User;

@Repository
public interface UserDAO extends JpaRepository<User, UUID> {
	
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findByEmailAndPassword(String email, String password);


}