package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    Optional<Semester> findSemesterByStartDateBeforeAndEndDateAfter(OffsetDateTime currentDateB, OffsetDateTime currentDateA);
}
