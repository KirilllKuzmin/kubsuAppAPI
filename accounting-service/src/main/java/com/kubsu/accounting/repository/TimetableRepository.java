package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Course;
import com.kubsu.accounting.model.Lecturer;
import com.kubsu.accounting.model.Semester;
import com.kubsu.accounting.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    @Query("SELECT DISTINCT t.course FROM Timetable t where t.lecturer = :lecturer")
    Optional<List<Course>> findDistinctCoursesByLecturer(Lecturer lecturer);

    Optional<Set<Timetable>> findAllByCourse(Course course);

    Optional<List<Timetable>> findAllByCourseAndSemester(Course course, Semester semester);
}
