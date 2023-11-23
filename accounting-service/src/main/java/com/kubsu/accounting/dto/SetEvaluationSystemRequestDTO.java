package com.kubsu.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetEvaluationSystemRequestDTO {

    private Double minGrade;

    private Double maxGrade;

    private Double passingGrade;
}
