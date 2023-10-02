package com.kubsu.report.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class ReportController {

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
