package com.kubsu.accounting.dto;

import com.kubsu.accounting.model.NumberTimeClassHeld;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumTimeClassHeldResponseDTO {

    private Long id;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    public NumTimeClassHeldResponseDTO(NumberTimeClassHeld numberTimeClassHeld) {
        id = numberTimeClassHeld.getId();
        startTime = numberTimeClassHeld.getStartTime();
        endTime = numberTimeClassHeld.getEndTime();
    }
}
