package com.kubsu.userService.controller.dto;

import com.kubsu.userService.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long userId;

    private String username;

    private String fullName;

    public StudentResponseDTO(User user) {
        userId = user.getId();
        username = user.getUsername();
        fullName = user.getFullName();
    }
}
