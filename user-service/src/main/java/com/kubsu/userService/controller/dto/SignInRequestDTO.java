package com.kubsu.userService.controller.dto;

import lombok.Data;

@Data
public class SignInRequestDTO {
    private String username;
    private String password;
}
