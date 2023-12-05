package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationTypeRepository extends JpaRepository<EvaluationType, Long> {
}
