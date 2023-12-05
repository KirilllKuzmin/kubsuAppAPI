package com.kubsu.accounting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "evaluation_grade_systems", schema = "accounting_schema")
public class EvaluationGradeSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_grade")
    private Double minGrade;

    @Column(name = "max_grade")
    private Double maxGrade;

    @Column(name = "passing_grade")
    private Double passingGrade;

    public EvaluationGradeSystem(Double minGrade, Double maxGrade, Double passingGrade) {
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
        this.passingGrade = passingGrade;
    }
}
