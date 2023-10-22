package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.model.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {

    @Query("SELECT wd.id FROM WorkDate wd WHERE wd.timetable in (:timetables)")
    Optional<List<Long>> findAllIdsByTimetables(List<Timetable> timetables);

    Optional<WorkDate> findByTimetable(Timetable timetable);
}