package com.kubsu.userService.controller.dto;

import com.kubsu.userService.model.Group;
import com.kubsu.userService.model.Specialty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDTO {

    private Long id;

    private String name;

    private Specialty specialty;

    public GroupResponseDTO(Group group) {
        id = group.getId();
        name = group.getName();
        specialty = group.getSpecialty();
    }
}
