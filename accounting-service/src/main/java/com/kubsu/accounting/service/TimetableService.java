package com.kubsu.accounting.service;

import com.kubsu.accounting.exception.LecturerNotFoundException;
import com.kubsu.accounting.exception.SemesterNotFoundException;
import com.kubsu.accounting.exception.TimetableNotFoundException;
import com.kubsu.accounting.model.Lecturer;
import com.kubsu.accounting.model.NumberTimeClassHeld;
import com.kubsu.accounting.model.Semester;
import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.repository.*;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TimetableService {

    private final NumberTimeClassHeldRepository numberTimeClassHeldRepository;

    private final StudentRepository studentRepository;

    private final LecturerRepository lecturerRepository;

    private final TimetableRepository timetableRepository;

    private final SemesterRepository semesterRepository;

    public TimetableService(NumberTimeClassHeldRepository numberTimeClassHeldRepository,
                            StudentRepository studentRepository,
                            LecturerRepository lecturerRepository,
                            TimetableRepository timetableRepository,
                            SemesterRepository semesterRepository) {
        this.numberTimeClassHeldRepository = numberTimeClassHeldRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.timetableRepository = timetableRepository;
        this.semesterRepository = semesterRepository;
    }

    public List<NumberTimeClassHeld> getAllNumTimeClassHeld() {
        return numberTimeClassHeldRepository.findAll();
    }

    public List<Timetable> getAllTimetable(OffsetDateTime startDate, OffsetDateTime endDate, Long userId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        List<Timetable> timetablesByWeek = new ArrayList<>();

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        if (studentRepository.existsByUserId(userId)) {
            return null; //ВР заглушка
        } else if (lecturerRepository.existsByUserId(userId)) {
            Lecturer lecturer = lecturerRepository.findLecturerByUserId(userId).orElseThrow(() ->
                    new LecturerNotFoundException("Unable to find lecturer with user_id=" + userId));

            List<Timetable> timetables = timetableRepository.findAllByLecturerAndSemester(lecturer, currentSemester)
                    .orElseThrow(() -> new TimetableNotFoundException("Unable to find any timetable in user_id=" + userId));

            for (Timetable timetable : timetables) {
                currentDate = startDate;

                while (!currentDate.isAfter(endDate)) {

                    if (currentDate.getDayOfWeek() == DayOfWeek.of(Math.toIntExact(timetable.getDayOfWeek())))
                        if (timetable.getWeekType() == null
                                || currentDate.get(WeekFields.ISO.weekOfWeekBasedYear()) % 2 == timetable.getWeekType().getId() % 2) {
                            timetablesByWeek.add(timetable);
                        }

                    currentDate = currentDate.plusDays(1);
                }
            }

            return timetablesByWeek;
        }

        return null;
    }
}
