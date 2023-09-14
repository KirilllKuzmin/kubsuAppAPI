package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
