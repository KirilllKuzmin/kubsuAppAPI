package com.kubsu.accounting.service;

import com.kubsu.accounting.model.*;
import com.kubsu.accounting.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountingServiceTest {

    @Mock
    private LecturerRepository lecturerRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TimetableRepository timetableRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private AbsenceTypeRepository absenceTypeRepository;

    @InjectMocks
    private AccountingService accountingService;

    @Test
    public void testSetAbsenceStudents() {
        Long studentId = 1L;
        Long lecturerId = 1L;
        Long courseId = 1L;
        OffsetDateTime absenceDate = OffsetDateTime.now();
        Long absenceTypeId = 1L;

        Student student = new Student();
        student.setGroupId(1L);
        when(studentRepository.findByUserId(eq(studentId))).thenReturn(Optional.of(student));

        Course course = new Course();
        when(courseRepository.findById(eq(courseId))).thenReturn(Optional.of(course));

        Lecturer lecturer = new Lecturer();
        when(lecturerRepository.findLecturerByUserId(eq(lecturerId))).thenReturn(Optional.of(lecturer));

        Long dayOfWeek = (long) absenceDate.getDayOfWeek().getValue();

        Long timetableId = 1L;
        when(timetableRepository.findByCourseAndLecturerAndDayOfWeekAndGroupId(eq(course), eq(lecturer), eq(dayOfWeek), anyLong()))
                .thenReturn(Optional.of(timetableId));

        Timetable timetable = new Timetable();
        when(timetableRepository.findById(eq(timetableId))).thenReturn(Optional.of(timetable));

        AbsenceType absenceType = new AbsenceType();
        when(absenceTypeRepository.findById(eq(absenceTypeId))).thenReturn(Optional.of(absenceType));

        String result = accountingService.setAbsenceStudents(studentId, lecturerId, courseId, absenceDate, absenceTypeId);

        assertEquals("Success", result);

        Mockito.verify(absenceRepository).save(any(Absence.class));
        Mockito.verify(absenceRepository, Mockito.never()).deleteAllById(anyList());
    }
}
