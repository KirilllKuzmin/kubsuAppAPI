package com.kubsu.userService.repository;

import com.kubsu.userService.model.DegreeOfStudy;
import com.kubsu.userService.model.Faculty;
import com.kubsu.userService.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Boolean existsByNameAndFacultyAndDegreeOfStudy(String name, Faculty faculty, DegreeOfStudy degreeOfStudy);

    Optional<Specialty> findByNameAndFacultyAndDegreeOfStudy(String name, Faculty faculty, DegreeOfStudy degreeOfStudy);
}
