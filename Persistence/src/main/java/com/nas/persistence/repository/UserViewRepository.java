package com.nas.persistence.repository;

import com.nas.persistence.model.UserView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserViewRepository extends JpaRepository<UserView, Long> {
    UserView findByEmail(String email);

    Optional<UserView> findById(Long email);
}