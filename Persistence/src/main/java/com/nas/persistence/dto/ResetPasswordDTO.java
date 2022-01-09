package com.nas.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordDTO {
    private String currentPassword;
    private String password1;
    private String password2;
    private String token;
}