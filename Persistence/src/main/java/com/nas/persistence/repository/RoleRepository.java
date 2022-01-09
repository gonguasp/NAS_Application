package com.nas.persistence.repository;

import com.nas.persistence.model.Role;
import com.nas.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByUser(User user);

    void deleteByUser(User user);
}