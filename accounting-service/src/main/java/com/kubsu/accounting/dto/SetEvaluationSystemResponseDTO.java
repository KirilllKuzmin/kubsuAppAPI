package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.EvaluationGradeSystem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetEvaluationSystemResponseDTO {

    private Long id;

    private Double minGrade;

    private Double maxGrade;

    private Double passingGrade;

    public SetEvaluationSystemResponseDTO(EvaluationGradeSystem evaluationGradeSystem) {
        id = evaluationGradeSystem.getId();
        minGrade = evaluationGradeSystem.getMinGrade();
        maxGrade = evaluationGradeSystem.getMaxGrade();
        passingGrade = evaluationGradeSystem.getPassingGrade();
    }
}
