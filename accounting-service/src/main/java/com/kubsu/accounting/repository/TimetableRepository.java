package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    @Query("SELECT DISTINCT t.course FROM Timetable t where t.lecturer = :lecturer")
    Optional<List<Course>> findDistinctCoursesByLecturer(Lecturer lecturer);

    Optional<Set<Timetable>> findAllByCourseAndLecturer(Course course, Lecturer lecturer);

    Optional<List<Timetable>> findAllByLecturerAndSemester(Lecturer lecturer,
                                                           Semester semester);

    @Query("SELECT DISTINCT t.id " +
            " FROM Timetable t " +
            " JOIN TimetableGroup tg " +
            "   ON t.id = tg.timetable.id " +
            "WHERE t.course = :course " +
            "  AND t.lecturer = :lecturer " +
            "  AND t.dayOfWeek = :dayOfWeek " +
            "  AND tg.groupId = :groupId")
    Optional<Long> findByCourseAndLecturerAndDayOfWeekAndGroupId(Course course,
                                                                 Lecturer lecturer,
                                                                 Long dayOfWeek,
                                                                 Long groupId);

    @Query("SELECT t.id " +
            " FROM Timetable t " +
            " JOIN TimetableGroup tg " +
            "   ON t.id = tg.timetable.id " +
            "WHERE t.course = :course " +
            "  AND t.semester = :semester " +
            "  AND t.lecturer = :lecturer " +
            "  AND tg.groupId = :groupId")
    Optional<List<Long>> findAllByCourseAndSemesterAndLecturerAndGroup(Course course,
                                                                            Semester semester,
                                                                            Lecturer lecturer,
                                                                            Long groupId);

    @Query("SELECT t.id " +
            " FROM Timetable t " +
            " JOIN TimetableGroup tg " +
            "   ON t.id = tg.timetable.id " +
            "WHERE t.course = :course " +
            "  AND t.semester = :semester " +
            "  AND t.lecturer = :lecturer " +
            "  AND tg.groupId = :groupId " +
            "  AND t.dayOfWeek = :dayOfWeek")
    Optional<Long> findAllByCourseAndSemesterAndLecturerAndGroupAndDayOfWeek(Course course,
                                                                                   Semester semester,
                                                                                   Lecturer lecturer,
                                                                                   Long groupId,
                                                                                   Long dayOfWeek);
}
