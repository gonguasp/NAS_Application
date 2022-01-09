package com.nas.persistence.repository;

import com.nas.persistence.model.User;
import com.nas.persistence.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    void deleteByUser(User user);
}