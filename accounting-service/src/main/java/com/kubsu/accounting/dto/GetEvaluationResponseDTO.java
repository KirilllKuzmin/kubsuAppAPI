package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEvaluationResponseDTO {

    private Student student;

    private OffsetDateTime evaluationDate;

    private EvaluationGrade evaluationGrade;

    public GetEvaluationResponseDTO(Evaluation evaluation) {
        student = evaluation.getStudent();
        evaluationDate = evaluation.getEvaluationDate();
        evaluationGrade = evaluation.getEvaluationGrade();
    }
}
