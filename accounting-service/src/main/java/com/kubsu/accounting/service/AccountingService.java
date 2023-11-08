package com.kubsu.accounting.service;

import com.kubsu.accounting.exception.*;
import com.kubsu.accounting.model.*;
import com.kubsu.accounting.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

    private final WorkDateRepository workDateRepository;

    private final TypeOfWorkRepository typeOfWorkRepository;

    private final EvaluationRepository evaluationRepository;

    private final EvaluationTypeRepository evaluationTypeRepository;

    public AccountingService(LecturerRepository lecturerRepository,
                             StudentRepository studentRepository,
                             TimetableRepository timetableRepository,
                             TimetableGroupRepository timetableGroupRepository,
                             SemesterRepository semesterRepository,
                             CourseRepository courseRepository,
                             AbsenceRepository absenceRepository,
                             AbsenceTypeRepository absenceTypeRepository,
                             WorkDateRepository workDateRepository,
                             TypeOfWorkRepository typeOfWorkRepository,
                             EvaluationRepository evaluationRepository,
                             EvaluationTypeRepository evaluationTypeRepository) {
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
        this.timetableGroupRepository = timetableGroupRepository;
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.absenceRepository = absenceRepository;
        this.absenceTypeRepository = absenceTypeRepository;
        this.workDateRepository = workDateRepository;
        this.typeOfWorkRepository = typeOfWorkRepository;
        this.evaluationRepository = evaluationRepository;
        this.evaluationTypeRepository = evaluationTypeRepository;
    }

    public List<Course> lecturerCourses(Long userId) {
        Lecturer lecturer = lecturerRepository.findLecturerByUserId(userId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id" + userId));

        return timetableRepository.findDistinctCoursesByLecturer(lecturer).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find courses from the lecturer with user_id" + userId))
                .stream()
                .sorted(Comparator.comparing(Course::getName))
                .collect(Collectors.toList());
    }

    public Set<Long> courseGroups(Long courseId, Long userId) {
        Set<Timetable> timetables = timetableRepository
                .findAllByCourseAndLecturer(courseRepository
                        .findById(courseId).orElseThrow(() ->
                                new CourseNotFoundException("Unable to find course_id" + courseId)),
                        lecturerRepository.findLecturerByUserId(userId).orElseThrow(() ->
                                new LecturerNotFoundException("Unable to find lecturer with user_id" + userId)))
                .orElseThrow(() ->
                        new TimetableNotFoundException("Unable to find timetables with course_id" + courseId));

        return timetableGroupRepository.findAllByTimetables(timetables);
    }

    public List<Student> getStudentsByGroup(Long groupId) {
        return studentRepository.findAllByGroupId(groupId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find students with group id " + groupId));
    }

    public List<OffsetDateTime> getDatesOfCourse(Long courseId, Long groupId, Long lecturerId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        List<OffsetDateTime> courseDates = new ArrayList<>();

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id" + lecturerId));

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        List<Long> timetableIds = timetableRepository.findAllByCourseAndSemesterAndLecturerAndGroup(
                courseRepository.findById(courseId).orElseThrow(() ->
                        new CourseNotFoundException("Unable to find course with id " + courseId)), currentSemester, lecturer, groupId)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id" + courseId));

        List<Timetable> timetables = timetableRepository.findAllById(timetableIds);

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

    public String setAbsenceStudents(Long studentId, Long lecturerId, Long courseId, OffsetDateTime absenceDate, Long absenceTypeId) {

        Student student = studentRepository.findByUserId(studentId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find student with user_id = " + studentId));

        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                                new CourseNotFoundException("Unable to find course_id" + courseId));

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id = " + lecturerId));

        Long dayOfWeek = (long) absenceDate.getDayOfWeek().getValue();

        //Ye;
        Long timetableId = timetableRepository.findByCourseAndLecturerAndDayOfWeek(course, lecturer, dayOfWeek).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable"));

        Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable with id = " + timetableId));

        if (absenceTypeId == null) {
            List<Long> absenceIdsToDelete = absenceRepository.findAllByStudentAndTimetableAndAbsenceDate(student, timetable, absenceDate)
                    .orElseThrow(() -> new AbsenceNotFoundException("Unable to find absence" + student + timetable + absenceDate));
            absenceRepository.deleteAllById(absenceIdsToDelete);

            return "Remove success";
        }
        AbsenceType absenceType = absenceTypeRepository.findById(absenceTypeId).orElseThrow(() ->
                new AbsenceTypeNotFoundException("Unable to find absenceType with id = " + absenceTypeId));

        absenceRepository.save(new Absence(timetable, student, absenceDate, OffsetDateTime.now(), absenceType));

        return "Success";
    }

    public List<Absence> getAbsenceStudents(Long groupId, Long lecturerId, Long courseId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new CourseNotFoundException("Unable to find course_id" + courseId));

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id = " + lecturerId));

        List<Long> timetableIds = timetableRepository.findAllByCourseAndSemesterAndLecturerAndGroup(
                        course, currentSemester, lecturer, groupId)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id" + courseId));

        List<Timetable> timetables = timetableRepository.findAllById(timetableIds);

        List<Student> students = studentRepository.findAllByGroupId(groupId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find student"));

        List<Long> absenceIds = absenceRepository.findAllByStudentAndTimetable(students, timetables).orElseThrow(() ->
                new AbsenceNotFoundException("Unable to find absenceIds"));

        return absenceRepository.findAllById(absenceIds);
    }

    public List<WorkDate> getWorkDates(Long courseId, Long groupId, Long lecturerId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        List<OffsetDateTime> courseDates = new ArrayList<>();

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id" + lecturerId));

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        List<Long> timetableIds = timetableRepository.findAllByCourseAndSemesterAndLecturerAndGroup(
                        courseRepository.findById(courseId).orElseThrow(() ->
                                new CourseNotFoundException("Unable to find course with id " + courseId)), currentSemester, lecturer, groupId)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id" + courseId));

        List<Timetable> timetables = timetableRepository.findAllById(timetableIds);

        List<Long> workDateIds = workDateRepository.findAllIdsByTimetables(timetables).orElseThrow(() ->
                new WorkDateNotFoundException("unable to find work dates with timetables " + timetables));

        return workDateRepository.findAllById(workDateIds);
    }

    public List<TypeOfWork> getWorkTypes() {
        return typeOfWorkRepository.findAll();
    }

    public List<WorkDate> setWorks(Long courseId, Long groupId, Long lecturerId, List<Long> workTypeIds, OffsetDateTime workDate) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        log.info(workTypeIds.toString());

        List<OffsetDateTime> courseDates = new ArrayList<>();

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id=" + lecturerId));

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        Long dayOfWeek = (long) workDate.getDayOfWeek().getValue();

        Long timetableId = timetableRepository.findAllByCourseAndSemesterAndLecturerAndGroupAndDayOfWeek(
                        courseRepository.findById(courseId).orElseThrow(() ->
                                new CourseNotFoundException("Unable to find course with id " + courseId)), currentSemester, lecturer, groupId, dayOfWeek)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id=" + courseId));

        Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable with id: " + timetableId));

        if (workTypeIds.isEmpty()) {
            List<Long> workDateToDeleteIds = workDateRepository.findAllByTimetableAndDateOfWorkAndSemester(timetable, workDate, currentSemester)
                    .orElseThrow(() -> new WorkDateNotFoundException("unable to find work dates with timetables " + timetable));

            workDateRepository.deleteAllById(workDateToDeleteIds);

            return new ArrayList<WorkDate>();
        }

        List<TypeOfWork> typeOfWorks = typeOfWorkRepository.findAllById(workTypeIds);

        List<TypeOfWork> allTypeOfWorks = typeOfWorkRepository.findAll();

        for (TypeOfWork typeOfWork : allTypeOfWorks) {
            if (!workDateRepository.existsByTimetableAndWorkDateAndTypeOfWork(timetable, workDate, typeOfWork)) {
                if (typeOfWorks.contains(typeOfWork)) {
                    workDateRepository.save(new WorkDate(timetable, workDate, typeOfWork));
                }
            } else if (!typeOfWorks.contains(typeOfWork)) {
                Long workDateToDeleteId = workDateRepository.findByTimetableAndDateOfWorkAndTypeOfWork(timetable, workDate, typeOfWork)
                        .orElseThrow(() -> new WorkDateNotFoundException("unable to find work dates with timetables " + timetable));

                WorkDate workDateToDelete = workDateRepository.findById(workDateToDeleteId)
                        .orElseThrow(() -> new WorkDateNotFoundException("unable to find work dates with timetables " + timetable));

                workDateRepository.delete(workDateToDelete);
            }
        }

        List<Long> workDateIds = workDateRepository.findAllByTimetableAndDateOfWorkAndSemester(timetable, workDate, currentSemester).orElseThrow(() ->
                new WorkDateNotFoundException("unable to find work dates with timetables " + timetable));

        return workDateRepository.findAllById(workDateIds);
    }

    public String setEvaluationStudents(Long studentId, Long lecturerId, Long courseId, OffsetDateTime evaluationDate, Long evaluationTypeId) {

        Student student = studentRepository.findByUserId(studentId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find student with user_id = " + studentId));

        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new CourseNotFoundException("Unable to find course_id" + courseId));

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id = " + lecturerId));

        Long dayOfWeek = (long) evaluationDate.getDayOfWeek().getValue();

        //Ye;
        Long timetableId = timetableRepository.findByCourseAndLecturerAndDayOfWeek(course, lecturer, dayOfWeek).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable"));

        Timetable timetable = timetableRepository.findById(timetableId).orElseThrow(() ->
                new TimetableNotFoundException("Unable to find timetable with id = " + timetableId));

        if (evaluationTypeId == null) {
            List<Long> evaluationIdsToDelete = evaluationRepository.findAllByStudentAndTimetableAndEvaluationDate(student, timetable, evaluationDate)
                    .orElseThrow(() -> new EvaluationNotFoundException("Unable to find evaluation" + student + timetable + evaluationDate));
            evaluationRepository.deleteAllById(evaluationIdsToDelete);

            return "Remove success";
        }
        EvaluationType evaluationType = evaluationTypeRepository.findById(evaluationTypeId).orElseThrow(() ->
                new EvaluationTypeNotFoundException("Unable to find evaluationType with id = " + evaluationTypeId));

        evaluationRepository.save(new Evaluation(timetable, student, evaluationDate, OffsetDateTime.now(), evaluationType));

        return "Success";
    }

    public List<Evaluation> getEvaluationStudents(Long groupId, Long lecturerId, Long courseId) {
        OffsetDateTime currentDate = OffsetDateTime.now();

        Semester currentSemester = semesterRepository.findSemesterByStartDateBeforeAndEndDateAfter(currentDate, currentDate)
                .orElseThrow(() -> new SemesterNotFoundException("Unable to find semester in date current date"));

        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new CourseNotFoundException("Unable to find course_id" + courseId));

        Lecturer lecturer = lecturerRepository.findLecturerByUserId(lecturerId).orElseThrow(() ->
                new LecturerNotFoundException("Unable to find lecturer with user_id = " + lecturerId));

        List<Long> timetableIds = timetableRepository.findAllByCourseAndSemesterAndLecturerAndGroup(
                        course, currentSemester, lecturer, groupId)
                .orElseThrow(() -> new TimetableNotFoundException("Unable to find timetable with course_id" + courseId));

        List<Timetable> timetables = timetableRepository.findAllById(timetableIds);

        List<Student> students = studentRepository.findAllByGroupId(groupId).orElseThrow(() ->
                new StudentNotFoundException("Unable to find student"));

        List<Long> evaluationIds = evaluationRepository.findAllByStudentAndTimetable(students, timetables).orElseThrow(() ->
                new AbsenceNotFoundException("Unable to find absenceIds"));

        return evaluationRepository.findAllById(evaluationIds);
    }
}
