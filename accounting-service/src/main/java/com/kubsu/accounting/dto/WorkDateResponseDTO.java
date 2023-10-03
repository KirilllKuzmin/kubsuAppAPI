package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.TypeOfWork;
import com.kubsu.accounting.model.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkDateResponseDTO {

    private TypeOfWork typeOfWork;

    private OffsetDateTime workDateTime;

    public WorkDateResponseDTO(WorkDate workDate) {
        typeOfWork = workDate.getTypeOfWork();
        workDateTime = workDate.getWorkDate();
    }
}
