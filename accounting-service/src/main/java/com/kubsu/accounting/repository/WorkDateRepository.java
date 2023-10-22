package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Semester;
import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.model.WorkDate;
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
}