package com.nas.persistence.dto;

import lombok.NoArgsConstructor;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class OperationDTO {
    public enum types {
        REGISTER,
        RESET_PASSWORD
    }

    public boolean isPresent(String value) {
        return Stream.of(OperationDTO.types.values()).map(OperationDTO.types::name).collect(Collectors.toList()).contains(value);
    }


}