package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.EvaluationGradeSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationGradeSystemRepository extends JpaRepository<EvaluationGradeSystem, Long> {

    Boolean existsByMinGradeAndMaxGradeAndPassingGrade(Double minGrade, Double maxGrade, Double passingGrade);

    Optional<EvaluationGradeSystem> findByMinGradeAndMaxGradeAndPassingGrade(Double minGrade, Double maxGrade, Double passingGrade);
}
