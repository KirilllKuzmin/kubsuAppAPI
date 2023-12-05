package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {

    @Query("SELECT wd.id FROM WorkDate wd WHERE wd.timetable in (:timetables)")
    Optional<List<Long>> findAllIdsByTimetables(List<Timetable> timetables);

    @Query("SELECT wd.id " +
            " FROM WorkDate wd " +
            " JOIN Timetable t " +
            "   ON wd.timetable.id = t.id " +
            "WHERE wd.timetable = :timetable " +
            "  AND t.dayOfWeek = :dayOfWeek " +
            "  AND t.semester = :semester")
    Optional<WorkDate> findByTimetableAndDateOfWorkAndSemester(Timetable timetable,
                                                               Long dayOfWeek,
                                                               Semester semester); //добавление семестра избыточно, поскольку в расписании и так он хранится!

    @Query("SELECT wd.id " +
            " FROM WorkDate wd " +
            " JOIN Timetable t " +
            "   ON wd.timetable.id = t.id " +
            "WHERE wd.timetable = :timetable " +
            "  AND wd.workDate = :workDate " +
            "  AND t.semester = :semester")
    Optional<List<Long>> findAllByTimetableAndDateOfWorkAndSemester(Timetable timetable,
                                                                    OffsetDateTime workDate,
                                                                    Semester semester);

    @Query("SELECT wd.id " +
            " FROM WorkDate wd " +
            " JOIN Timetable t " +
            "   ON wd.timetable.id = t.id " +
            "WHERE wd.timetable = :timetable " +
            "  AND wd.workDate = :workDate " +
            "  AND wd.typeOfWork = :typeOfWork " +
            "  AND wd.evaluationGradeSystem = :evaluationGradeSystem")
    Optional<Long> findByTimetableAndDateOfWorkAndTypeOfWorkAndEvaluationGradeSystem(Timetable timetable,
                                                                                     OffsetDateTime workDate,
                                                                                     TypeOfWork typeOfWork,
                                                                                     EvaluationGradeSystem evaluationGradeSystem);

    @Query("SELECT wd.id " +
            " FROM WorkDate wd " +
            " JOIN Timetable t " +
            "   ON wd.timetable.id = t.id " +
            "WHERE wd.timetable = :timetable " +
            "  AND wd.workDate = :workDate " +
            "  AND wd.typeOfWork = :typeOfWork")
    Optional<Long> findByTimetableAndDateOfWorkAndTypeOfWork(Timetable timetable,
                                                             OffsetDateTime workDate,
                                                             TypeOfWork typeOfWork);

    Boolean existsByTimetableAndWorkDateAndTypeOfWork(Timetable timetable,
                                                      OffsetDateTime workDate,
                                                      TypeOfWork typeOfWork);

    Boolean existsByTimetableAndWorkDateAndTypeOfWorkAndEvaluationGradeSystem(Timetable timetable,
                                                                              OffsetDateTime workDate,
                                                                              TypeOfWork typeOfWork,
                                                                              EvaluationGradeSystem evaluationGradeSystem);
}