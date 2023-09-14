package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.AbsenceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceTypeRepository extends JpaRepository<AbsenceType, Long> {
}
