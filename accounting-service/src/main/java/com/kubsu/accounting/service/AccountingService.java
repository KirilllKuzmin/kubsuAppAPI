package com.kubsu.accounting.service;

import com.kubsu.accounting.exception.CourseNotFoundException;
import com.kubsu.accounting.exception.LecturerNotFoundException;
import com.kubsu.accounting.exception.StudentNotFoundException;
import com.kubsu.accounting.exception.TimetableNotFoundException;
import com.kubsu.accounting.model.Course;
import com.kubsu.accounting.model.Lecturer;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountingService {

    private final LecturerRepository lecturerRepository;

    private final StudentRepository studentRepository;

    private final TimetableRepository timetableRepository;

    private final TimetableGroupRepository timetableGroupRepository;

    private final CourseRepository courseRepository;

    public AccountingService(LecturerRepository lecturerRepository,
                             StudentRepository studentRepository,
                             TimetableRepository timetableRepository,
                             TimetableGroupRepository timetableGroupRepository,
                             CourseRepository courseRepository) {
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
        this.timetableRepository = timetableRepository;
        this.timetableGroupRepository = timetableGroupRepository;
        this.courseRepository = courseRepository;
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
}
