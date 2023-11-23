package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Evaluation;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.model.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e.id FROM Evaluation e WHERE e.student IN (:students) AND e.workDate IN (:workDates)")
    Optional<List<Long>> findAllByStudentAndWorkDate(List<Student> students, List<WorkDate> workDates);

    @Query("SELECT e.id " +
            " FROM Evaluation e " +
            "WHERE e.student = :student " +
            "  AND e.workDate = :workDate " +
            "  AND e.evaluationDate = :evaluationDate")
    Optional<List<Long>> findAllByStudentAndWorkDateAndEvaluationDate(Student student,
                                                                      WorkDate workDate,
                                                                      OffsetDateTime evaluationDate);
}
