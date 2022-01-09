package com.nas.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterDTO {
    private String email;
    private String name;
    private String password1;
    private String password2;
}