package com.kubsu.accounting.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyResponseDTO {

    private Long id;

    private String name;

    private FacultyResponseDTO faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "degree_of_study_id")
    private DegreeOfStudyResponseDTO degreeOfStudy;
}
