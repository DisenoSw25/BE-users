package edu.uclm.esi.users.dao;

import edu.uclm.esi.users.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenDAO extends JpaRepository<PasswordResetToken, String> {}
