package com.kubsu.accounting.service;

import com.kubsu.accounting.exception.*;
import com.kubsu.accounting.model.*;
import com.kubsu.accounting.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class AccountingService {

    private final LecturerRepository lecturerRepository;

    private final StudentRepository studentRepository;

    private final TimetableRepository timetableRepository;

    private final TimetableGroupRepository timetableGroupRepository;

    private final SemesterRepository semesterRepository;

    private final CourseRepository courseRepository;

    private final AbsenceRepository absenceRepository;

    private final AbsenceTypeRepository absenceTypeRepository;

    public AccountingService(LecturerRepository lecturerRepository,
                             StudentRepository studentRepository,
                             TimetableRepository timetableRepository,
                             TimetableGroupRepository timetableGroupRepository,
                             SemesterRepository semesterRepository,
                             CourseRepository courseRepository,
                             AbsenceRepository absenceRepository,
                             AbsenceTypeRepository absenceTypeRepository) {
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
        this.timetableGroupRepository = timetableGroupRepository;
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.absenceRepository = absenceRepository;
        this.absenceTypeRepository = absenceTypeRepository;
    }

    public List<Course> lecturerCourses(Long userId) {
        Lecturer lecturer = lecturerRepository.findLecturerByUserId(userId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id" + userId));

        return timetableRepository.findDistinctCoursesByLecturer(lecturer).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find courses from the lecturer with user_id" + userId));
    }

    public Set<Long> courseGroups(Long courseId) {
        Set<Timetable> timetables = timetableRepository
                .findAllByCourse(courseRepository
                        .findById(courseId).orElseThrow(() ->
                                new CourseNotFoundException("Unable to find course_id" + courseId)))
                .orElseThrow(() ->
                        new TimetableNotFoundException("Unable to find timetables with course_id" + courseId));

        return timetableGroupRepository.findAllByTimetables(timetables);
    }

    public List<Student> getStudentsByGroup(Long groupId) {
        return studentRepository.findAllByGroupId(groupId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find students with group id " + groupId));
    }

    public List<OffsetDateTime> getDatesOfCourse(Long courseId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        List<OffsetDateTime> courseDates = new ArrayList<>();

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        List<Timetable> timetables = timetableRepository.findAllByCourseAndSemester(
                courseRepository.findById(courseId).orElseThrow(() ->
                        new CourseNotFoundException("Unable to find course with id " + courseId)), currentSemester)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id" + courseId));

        for (Timetable timetable : timetables) {
            //Переопределяем дату на начало, для подсчета всех дат, когда должна проводится пара
            currentDate = currentSemester.getStartDate();

            while (!currentDate.isAfter(currentSemester.getEndDate())) {

                if (currentDate.getDayOfWeek() == DayOfWeek.of(Math.toIntExact(timetable.getDayOfWeek())))
                    if (timetable.getWeekType() == null
                            || currentDate.get(WeekFields.ISO.weekOfWeekBasedYear()) % 2 == timetable.getWeekType().getId() % 2) {
                        courseDates.add(currentDate);
                }

                currentDate = currentDate.plusDays(1);
            }
        }

        courseDates.sort(Comparator.naturalOrder());

        return courseDates;
    }

    public String setAbsenceStudents(Long userId, Long timetableId, OffsetDateTime absenceDate, Long absenceTypeId) {

        Student student = studentRepository.findByUserId(userId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find student with user_id = " + userId));

        AbsenceType absenceType = absenceTypeRepository.findById(absenceTypeId).orElseThrow(() ->
                new AbsenceTypeNotFoundException("Unable to find absenceType with id = " + absenceTypeId));

        Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable with id = " + timetableId));

        absenceRepository.save(new Absence(timetable, student, absenceDate, OffsetDateTime.now(), absenceType));

        return "Success";
    }
}
