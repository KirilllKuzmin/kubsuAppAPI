package com.kubsu.userService.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {

    private String token;

    private String userId;

    private String username;

    private List<String> roles;
}
