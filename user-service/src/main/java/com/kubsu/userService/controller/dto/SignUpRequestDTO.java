package com.kubsu.userService.controller.dto;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String username;
    private String password;
}