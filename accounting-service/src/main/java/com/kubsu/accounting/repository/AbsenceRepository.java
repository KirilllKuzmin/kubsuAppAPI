package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Absence;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    @Query("SELECT a.id FROM Absence a WHERE a.student IN (:students) AND a.timetable IN (:timetables)")
    Optional<List<Long>> findAllByStudentAndTimetable(List<Student> students, List<Timetable> timetables);

    @Query("SELECT a.id " +
            " FROM Absence a " +
            "WHERE a.student = :student " +
            "  AND a.timetable = :timetable " +
            "  AND a.absenceDate = :absenceDate")
    Optional<List<Long>> findAllByStudentAndTimetableAndAbsenceDate(Student student,
                                                                    Timetable timetable,
                                                                    OffsetDateTime absenceDate);
}
