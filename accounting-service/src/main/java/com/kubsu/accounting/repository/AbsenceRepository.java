package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}
