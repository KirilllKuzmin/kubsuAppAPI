package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.TypeOfWork;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetWorkTypesRequestDTO {

    private Long typeOfWorkId;

    private Double minGrade;

    private Double maxGrade;

    private Double passingGrade;

    public SetWorkTypesRequestDTO(TypeOfWork typeOfWork) {
        typeOfWorkId = typeOfWork.getId();
    }
}
