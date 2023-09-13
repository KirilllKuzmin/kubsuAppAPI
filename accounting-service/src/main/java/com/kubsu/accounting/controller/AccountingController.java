package com.kubsu.accounting.controller;

import com.kubsu.accounting.dto.GroupResponseDTO;
import com.kubsu.accounting.dto.StudentResponseDTO;
import com.kubsu.accounting.model.Course;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.rest.UserServiceClient;
import com.kubsu.accounting.service.AccountingService;
import com.kubsu.accounting.service.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public UserDetailsImpl profile() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails;
    }

    @GetMapping("/lecturer")
    @PreAuthorize("hasRole('LECTURER')")
    public String lecturerAccess() {
        return "Lecturer access.";
    }

    @GetMapping("/lecturer/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public List<Course> lecturerCourses() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return accountingService.lecturerCourses(userDetails.getId());
    }

    @GetMapping("/courses/{courseId}/groups")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public List<GroupResponseDTO> courseGroups(@PathVariable Long courseId) {
        return userServiceClient.getGroups(new ArrayList<>(accountingService.courseGroups(courseId)));
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
}
