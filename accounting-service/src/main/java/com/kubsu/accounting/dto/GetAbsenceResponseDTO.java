package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.Absence;
import com.kubsu.accounting.model.AbsenceType;
import com.kubsu.accounting.model.Student;
import com.kubsu.accounting.model.Timetable;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAbsenceResponseDTO {

    private Student student;

    private OffsetDateTime absenceDate;

    private AbsenceType absenceType;

    public GetAbsenceResponseDTO(Absence absence) {
        student = absence.getStudent();
        absenceDate = absence.getAbsenceDate();
        absenceType = absence.getAbsenceType();
    }
}
