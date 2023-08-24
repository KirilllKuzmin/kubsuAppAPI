package com.kubsu.userService.repository;

import com.kubsu.userService.model.DegreeOfStudy;
import com.kubsu.userService.model.Faculty;
import com.kubsu.userService.model.Group;
import com.kubsu.userService.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Boolean existsByNameAndSpecialty(String name, Specialty specialty);

    Optional<Group> findByNameAndSpecialty(String name, Specialty specialty);
}
