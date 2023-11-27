package com.kubsu.report.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    @GetMapping("/generate-docx-report")
    public ResponseEntity<byte[]> generateDocxReport() throws IOException {
        // Создаем новый документ DOCX
        XWPFDocument document = new XWPFDocument();

        // Создаем раздел и параграфы (пример)
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("Пример данных в DOCX отчете");

        // Генерируем DOCX-файл в памяти
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);

        // Устанавливаем заголовки для ответа
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report.docx");

        // Отправляем DOCX-файл в ответе
        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @GetMapping(value = "/absences/groups/{groupNumber}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateReport(@PathVariable Long groupNumber) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        List<Map<String, Object>> courses = getCourses(groupNumber);

        try (Workbook workbook = new XSSFWorkbook()) {

            for (Map<String, Object> course : courses) {

                List<Map<String, Object>> reportData = getReportDataFromDatabase(groupNumber, (Integer) course.get("id"));

                String courseName = (String) course.get("course_type") + " " + (String) course.get("name");

                createSheet(workbook, reportData, courseName);

                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputStream.size())
                .body(outputStream.toByteArray());
    }

    private void createSheet(Workbook workbook, List<Map<String, Object>> reportData, String courseName) {

        Sheet sheet = workbook.createSheet(courseName);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ФИО");
        headerRow.createCell(1).setCellValue("Количество пропусков");

        int rowNum = 1;
        for (Map<String, Object> data : reportData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(String.valueOf(data.get("full_name")));
            row.createCell(1).setCellValue(String.valueOf(data.get("count_absence")));
        }
    }

    private List<Map<String, Object>> getReportDataFromDatabase(Long groupNumber, Integer courseId) {
        String sql = "select u.id, u.full_name, count(s.id) as count_absence, c.id, c.name, ct.name\n" +
                "    from accounting_schema.absences a\n" +
                "    join accounting_schema.students s\n" +
                "        on a.student_id = s.id\n" +
                "    join user_schema.users u\n" +
                "        on s.user_id = u.id\n" +
                "    join accounting_schema.timetables t\n" +
                "        on a.timetable_id = t.id\n" +
                "    join accounting_schema.courses c\n" +
                "        on c.id = t.course_id\n" +
                "    join accounting_schema.course_types ct\n" +
                "        on c.course_type_id = ct.id\n" +
                "    where s.group_id = (?)\n" +
                "       and c.id = (?)\n" +
                " group by s.id, u.id, c.id, ct.id";
        return jdbcTemplate.queryForList(sql, groupNumber, courseId);
    }

    private List<Map<String, Object>> getCourses(Long groupNumber) {
        String sql = "select c.id, c.name, ct.name as course_type\n" +
                "    from accounting_schema.absences a\n" +
                "    join accounting_schema.timetables t\n" +
                "        on a.timetable_id = t.id\n" +
                "   join accounting_schema.timetable_groups tg\n" +
                "       on t.id = tg.timetable_id" +
                "    join accounting_schema.courses c\n" +
                "        on c.id = t.course_id\n" +
                "    join accounting_schema.course_types ct\n" +
                "        on c.course_type_id = ct.id\n" +
                "   where tg.group_id = (?)\n" +
                " group by c.id, ct.id" +
                " order by c.id desc";
        return jdbcTemplate.queryForList(sql, groupNumber);
    }
}
