package com.kubsu.accounting.controller;

import com.kubsu.accounting.dto.GroupResponseDTO;
import com.kubsu.accounting.dto.StudentResponseDTO;
import com.kubsu.accounting.model.Course;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.rest.UserServiceClient;
import com.kubsu.accounting.service.AccountingService;
import com.kubsu.accounting.service.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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

    @PostMapping("/lecturers/setAbsences")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR')")
    public ResponseEntity<?> setAbsenceStudent(@RequestBody Long studentId,
                                               Long courseId,
                                               OffsetDateTime absenceDate,
                                               Long absenceTypeId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(accountingService.setAbsenceStudents(studentId, userDetails.getId(), courseId, absenceDate, absenceTypeId));
    }

    @GetMapping("/generate-report")
    public ResponseEntity<byte[]> generateReport() throws IOException {
        // Создаем новую книгу Excel
        Workbook workbook = new XSSFWorkbook();
        // Создаем лист
        Sheet sheet = workbook.createSheet("Отчет");

        // Создаем и заполняем ячейки (пример)
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Пример данных");

        // Генерируем XLSX-файл в памяти
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        // Устанавливаем заголовки для ответа
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report.xlsx");

        // Отправляем XLSX-файл в ответе
        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}
