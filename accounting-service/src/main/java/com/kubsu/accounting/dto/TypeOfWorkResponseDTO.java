package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.TypeOfWork;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeOfWorkResponseDTO {

    private Long id;

    private String name;

    public TypeOfWorkResponseDTO(TypeOfWork typeOfWork) {
        id = typeOfWork.getId();
        name = typeOfWork.getName();
    }
}
