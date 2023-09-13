package com.kubsu.accounting.repository;

import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.model.TimetableGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface TimetableGroupRepository extends JpaRepository<TimetableGroup, Long> {

    @Query("SELECT t.groupId FROM TimetableGroup t where t.timetable IN (:timetables)")
    Set<Long> findAllByTimetables(Set<Timetable> timetables);
}
