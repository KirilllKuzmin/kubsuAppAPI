package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evaluation_grades", schema = "accounting_schema")
public class EvaluationGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_grade_system_id")
    private EvaluationGradeSystem evaluationGradeSystem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evaluation_type_id")
    private EvaluationType evaluationType;

    @Column(name = "point_number")
    private Double pointNumber;

    public EvaluationGrade(EvaluationGradeSystem evaluationGradeSystem, EvaluationType evaluationType, Double pointNumber) {
        this.evaluationGradeSystem = evaluationGradeSystem;
        this.evaluationType = evaluationType;
        this.pointNumber = pointNumber;
    }
}
