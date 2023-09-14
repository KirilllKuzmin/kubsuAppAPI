package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<List<Student>> findAllByGroupId(Long groupId);

    Optional<Student> findByUserId(Long userId);
}
