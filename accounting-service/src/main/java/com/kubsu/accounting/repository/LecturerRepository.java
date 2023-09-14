package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findLecturerByUserId(Long userId);
}
