package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Evaluation;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e.id FROM Evaluation e WHERE e.student IN (:students) AND e.timetable IN (:timetables)")
    Optional<List<Long>> findAllByStudentAndTimetable(List<Student> students, List<Timetable> timetables);

    @Query("SELECT e.id " +
            " FROM Evaluation e " +
            "WHERE e.student = :student " +
            "  AND e.timetable = :timetable " +
            "  AND e.evaluationDate = :evaluationDate")
    Optional<List<Long>> findAllByStudentAndTimetableAndEvaluationDate(Student student,
                                                                    Timetable timetable,
                                                                    OffsetDateTime evaluationDate);
}
