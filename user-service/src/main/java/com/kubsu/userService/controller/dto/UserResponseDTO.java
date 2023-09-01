package com.kubsu.userService.controller.dto;

import com.kubsu.userService.model.Group;
import com.kubsu.userService.model.Role;
import com.kubsu.userService.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private Long kubsuUserId;

    private String username;

    private String fullName;

    private String email;

    private Group group;

    private OffsetDateTime startEducationDate;

    private OffsetDateTime endEducationDate;

    private OffsetDateTime creationDate;

    private Set<Role> authorities;

    public UserResponseDTO(User user) {
        id = user.getId();
        kubsuUserId = user.getKubsuUserId();
        username = user.getUsername();
        fullName = user.getFullName();
        email = user.getEmail();
        group = user.getGroup();
        startEducationDate = user.getStartEducationDate();
        endEducationDate = user.getEndEducationDate();
        creationDate = user.getCreationDate();
        authorities = user.getRoles();
    }
}
