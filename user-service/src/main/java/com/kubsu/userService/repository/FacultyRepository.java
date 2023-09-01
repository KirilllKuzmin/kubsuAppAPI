package com.kubsu.userService.repository;

import com.kubsu.userService.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Boolean existsByName(String name);

    Optional<Faculty> findByName(String name);
}
