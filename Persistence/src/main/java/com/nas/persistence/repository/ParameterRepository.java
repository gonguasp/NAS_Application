package com.nas.persistence.repository;

import com.nas.persistence.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Parameter findByName(String name);

    Parameter findByValue(String value);
}