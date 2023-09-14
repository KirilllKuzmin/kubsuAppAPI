package com.kubsu.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDTO {

    private Long id;

    private String name;

    private SpecialtyResponseDTO specialty;
}
