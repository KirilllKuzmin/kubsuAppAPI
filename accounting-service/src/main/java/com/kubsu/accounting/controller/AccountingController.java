package com.kubsu.accounting.controller;

import com.kubsu.accounting.dto.*;
import com.kubsu.accounting.model.*;
import com.kubsu.accounting.rest.UserServiceClient;
import com.kubsu.accounting.service.AccountingService;
import com.kubsu.accounting.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/accounting")
public class AccountingController {

    private final AccountingService accountingService;

    private final UserServiceClient userServiceClient;

    public AccountingController(AccountingService accountingService, UserServiceClient userServiceClient) {
        this.accountingService = accountingService;
        this.userServiceClient = userServiceClient;
    }

    @GetMapping("/access")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/profiles")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public UserDetailsImpl profile() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails;
    }

    @GetMapping("/lecturers/access")
    @PreAuthorize("hasRole('LECTURER')")
    public String lecturerAccess() {
        return "Lecturer access.";
    }

    @GetMapping("/lecturers/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public List<Course> lecturerCourses() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return accountingService.lecturerCourses(userDetails.getId());
    }

    @GetMapping("/lecturers/courses/{courseId}/groups")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public List<GroupResponseDTO> courseGroups(@PathVariable Long courseId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userServiceClient.getGroups(new ArrayList<>(accountingService.courseGroups(courseId, userDetails.getId())));
    }

    @GetMapping("/groups/{groupId}/students")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public List<StudentResponseDTO> groupStudents(@PathVariable Long groupId) {
        return userServiceClient.getStudents(accountingService
                .getStudentsByGroup(groupId)
                .stream()
                .map(Student::getUserId)
                .collect(Collectors.toList()));
    }

    @GetMapping("/lecturers/courses/{courseId}/groups/{groupId}/dates")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('STUDENT') or hasRole('ADMIN')")
    public List<OffsetDateTime> courseDates(@PathVariable Long courseId, @PathVariable Long groupId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return accountingService.getDatesOfCourse(courseId, groupId, userDetails.getId());
    }

    @PostMapping("/lecturers/absences")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public ResponseEntity<?> setAbsenceStudent(@RequestBody SetAbsenceRequestDTO setAbsenceRequestDTO) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(accountingService.setAbsenceStudents(setAbsenceRequestDTO.getStudentId(),
                userDetails.getId(),
                setAbsenceRequestDTO.getCourseId(),
                setAbsenceRequestDTO.getAbsenceDate(),
                setAbsenceRequestDTO.getAbsenceTypeId()));
    }

    @GetMapping("/lecturers/absences/courses/{courseId}/groups/{groupId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<GetAbsenceResponseDTO> getAbsences(@PathVariable Long courseId, @PathVariable Long groupId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Absence> absences = accountingService.getAbsenceStudents(groupId, userDetails.getId(), courseId);

        return absences
                .stream()
                .map(GetAbsenceResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/lecturers/courses/{courseId}/groups/{groupId}/works")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('STUDENT') or hasRole('ADMIN')")
    public List<WorkDateResponseDTO> workDates(@PathVariable Long courseId, @PathVariable Long groupId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return accountingService.getWorkDates(courseId, groupId, userDetails.getId())
                .stream()
                .map(WorkDateResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/workTypes")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('STUDENT') or hasRole('ADMIN')")
    public List<TypeOfWorkResponseDTO> workDates() {

        return accountingService.getWorkTypes()
                .stream()
                .map(TypeOfWorkResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/lecturers/courses/{courseId}/groups/{groupId}/dates/{workDate}/works")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public List<WorkDateResponseDTO> setWorkTypes(@PathVariable Long courseId,
                                            @PathVariable Long groupId,
                                            @PathVariable OffsetDateTime workDate,
                                            @RequestBody List<Long> workDates) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<WorkDate> workDateResponse = accountingService.setWorks(courseId, groupId, userDetails.getId(), workDates, workDate);

        return workDateResponse
                .stream()
                .map(WorkDateResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/lecturers/evaluations")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public ResponseEntity<?> setEvaluationStudent(@RequestBody SetEvaluationRequestDTO setEvaluationRequestDTO) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(accountingService.setEvaluationStudents(setEvaluationRequestDTO.getStudentId(),
                userDetails.getId(),
                setEvaluationRequestDTO.getCourseId(),
                setEvaluationRequestDTO.getEvaluationDate(),
                setEvaluationRequestDTO.getEvaluationTypeId()));
    }

    @GetMapping("/lecturers/evaluations/courses/{courseId}/groups/{groupId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<GetEvaluationResponseDTO> getEvaluations(@PathVariable Long courseId, @PathVariable Long groupId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<Evaluation> evaluations = accountingService.getEvaluationStudents(groupId, userDetails.getId(), courseId);

        return evaluations
                .stream()
                .map(GetEvaluationResponseDTO::new)
                .collect(Collectors.toList());
    }
}
